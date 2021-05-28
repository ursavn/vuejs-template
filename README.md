# amazon-tool
Automation crawl product and buy

# Tool
Visual studio code or Eclipse
Required:
   - Lombok 
   - JDK 8 or more
More extension for VS Code:
   - Java extension pack
   - Spring boot extension pack
   - Docker     

# Run chrome selenium on docker
  $ docker run -d -p 4444:4444 -v /dev/shm:/dev/shm selenium/standalone-chrome:4.0.0-beta-3-prerelease-20210321   
   
# Build backend
1. cd ./amz-backend
2. mvn clean
3. mvn -Dmaven.test.skip=true package

# Build frontend
1. cd ./amz-frontend
2. yarn build

# Deploy all service
   docker-compose up -d --build