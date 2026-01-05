# AWS Cloud Deployment Guide for Batavia

This guide outlines the recommended architecture and steps to deploy the Batavia Banking Middleware to Amazon Web Services (AWS) in a production-ready manner.

## 🏗️ Recommended Architecture: AWS ECS Fargate

For a banking middleware that requires high availability, scalability, and security without the operational overhead of managing servers, we recommend **AWS ECS (Elastic Container Service) with Fargate**.

### Why Fargate?
- **Serverless**: No EC2 instances to patch or manage.
- **Scalability**: Auto-scales based on CPU, Memory, or custom metrics (e.g., Transaction Per Second).
- **Security**: Strong isolation with VPC networking and IAM roles per task.
- **Cost-Effective**: Pay only for the vCPU and Memory resources used by the running containers.

---

## 1. Containerization

First, ensure the application is containerized. Create a `Dockerfile` in the project root:

```dockerfile
# Use a lightweight Java 17 runtime
FROM eclipse-temurin:17-jdk-alpine

# Add a volume pointing to /tmp
VOLUME /tmp

# Copy the built jar file
COPY target/batavia-0.0.1-SNAPSHOT.jar app.jar

# Expose the port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar","/app.jar"]
```

---

## 2. Infrastructure Components

To support the "Production-Inspired" requirements, your AWS environment should include:

### 🌐 Networking (VPC)
- **Public Subnets**: Host the **Application Load Balancer (ALB)** and **NAT Gateway**.
- **Private Subnets**: Host the **ECS Tasks** (Batavia application). This ensures the application is not directly accessible from the internet.

### ⚖️ Load Balancing
- **Application Load Balancer (ALB)**:
  - Handles SSL/TLS termination (HTTPS).
  - Routes traffic to the ECS Service.
  - Performs health checks on `/actuator/health`.

### 💾 Data & State (Future Proofing)
- **Amazon ElastiCache (Redis)**:
  - *Requirement*: To fully satisfy the "Horizontal Scalability" requirement for Idempotency.
  - *Action*: Implement a `RedisIdempotencyService` and connect it here.
- **Amazon RDS (PostgreSQL/Aurora)**:
  - If the application needs to persist transaction logs or audit trails in the future.

### 👁️ Observability
- **Amazon CloudWatch**: For logs (via `awslogs` driver) and metrics.
- **AWS X-Ray**: For distributed tracing (compatible with the Micrometer Tracing setup).

---

## 3. Deployment Steps (Manual / CLI)

### Step 1: Build and Push Docker Image
You need an **Amazon ECR (Elastic Container Registry)** repository.

```bash
# 1. Authenticate Docker to ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <aws_account_id>.dkr.ecr.us-east-1.amazonaws.com

# 2. Build the image
docker build -t batavia .

# 3. Tag the image
docker tag batavia:latest <aws_account_id>.dkr.ecr.us-east-1.amazonaws.com/batavia:latest

# 4. Push to ECR
docker push <aws_account_id>.dkr.ecr.us-east-1.amazonaws.com/batavia:latest
```

### Step 2: Create ECS Task Definition
Define how the container should run.
- **Launch Type**: FARGATE
- **CPU/Memory**: e.g., 1 vCPU / 2 GB
- **Environment Variables**:
  - `SPRING_PROFILES_ACTIVE`: `prod`
  - `MANAGEMENT_TRACING_SAMPLING_PROBABILITY`: `0.1` (Adjust for production volume)

### Step 3: Create ECS Service
- **Cluster**: Create a Fargate cluster.
- **Service**: Create a service using the Task Definition.
- **Desired Tasks**: Minimum 2 (for High Availability).
- **Load Balancer**: Attach the service to the ALB Target Group.

---

## 4. CI/CD Pipeline (GitHub Actions)

Automate the deployment process. Create `.github/workflows/deploy.yml`:

```yaml
name: Deploy to Amazon ECS

on:
  push:
    branches:
      - main

env:
  AWS_REGION: us-east-1
  ECR_REPOSITORY: batavia
  ECS_SERVICE: batavia-service
  ECS_CLUSTER: batavia-cluster
  ECS_TASK_DEFINITION: .aws/task-definition.json
  CONTAINER_NAME: batavia

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
    - name: Checkout
      uses: actions/checkout@v3

    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: maven

    - name: Build with Maven
      run: mvn clean package -DskipTests

    - name: Configure AWS credentials
      uses: aws-actions/configure-aws-credentials@v1
      with:
        aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
        aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
        aws-region: ${{ env.AWS_REGION }}

    - name: Login to Amazon ECR
      id: login-ecr
      uses: aws-actions/amazon-ecr-login@v1

    - name: Build, tag, and push image to Amazon ECR
      id: build-image
      env:
        ECR_REGISTRY: ${{ steps.login-ecr.outputs.registry }}
        IMAGE_TAG: ${{ github.sha }}
      run: |
        docker build -t $ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG .
        docker push $ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG
        echo "image=$ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG" >> $GITHUB_OUTPUT

    - name: Fill in the new image ID in the Amazon ECS task definition
      id: task-def
      uses: aws-actions/amazon-ecs-render-task-definition@v1
      with:
        task-definition: ${{ env.ECS_TASK_DEFINITION }}
        container-name: ${{ env.CONTAINER_NAME }}
        image: ${{ steps.build-image.outputs.image }}

    - name: Deploy Amazon ECS task definition
      uses: aws-actions/amazon-ecs-deploy-task-definition@v1
      with:
        task-definition: ${{ steps.task-def.outputs.task-definition }}
        service: ${{ env.ECS_SERVICE }}
        cluster: ${{ env.ECS_CLUSTER }}
        wait-for-service-stability: true
```

## 5. Security Checklist for Production

1.  **WAF (Web Application Firewall)**: Attach AWS WAF to the ALB to protect against common web exploits (SQLi, XSS).
2.  **Secrets Management**: Do not hardcode secrets. Use **AWS Systems Manager Parameter Store** or **AWS Secrets Manager** and inject them as environment variables into the ECS Task.
3.  **Least Privilege**: Ensure the ECS Task Execution Role has only the permissions it needs (e.g., pull from ECR, write to CloudWatch).
