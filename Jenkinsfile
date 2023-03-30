pipeline {
    agent any
    
    stages {
        stage('Building and create .jar file'){
            steps {
                echo 'Building the .jar file'
                
                //Builds and create our .jar file
                sh 'mvn clean package'
            }
        }
        
        stage('Creating Docker image') {
            steps {
                //Builds the image of our application
                sh 'sudo docker build -t connoreg/p2backend:latest .'
            }
        }

        stage('Deploying into docker container') {
            steps {
                //Stop any running containers of this image
                sh 'sudo docker rm -f $(sudo docker ps -af name=p2back -q)'
                
                //Run latest version of image in a container
                sh 'sudo docker run $dockerrunflags -p 4798:4798 --name p2back -e url=$dburl connoreg/p2backend:latest'
            }
        }
    }
}