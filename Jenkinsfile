pipeline {
    agent none

    environment {
        REGISTRY = 'harbor.edu.cn/nju27'
    }

    stages {
        stage('Clone Code') {
            agent {
                label 'master'
            }
            steps {//第一步：克隆代码，为了build image
                echo "1. Git Clone Stage"
                git branch: 'master', url: "https://gitee.com/austynDing/cloud_-native_-project_0.git"
            }
        }
        stage('Maven Build') {//第二步：maven打包
            agent {
                docker {
                    image 'maven:latest'
                    args '-v /root/.m2:/root/.m2' //把本地的 /root/.m2 挂载到容器的 /root/.m2目录下
                }
            }
            steps {
                echo "2. Maven Build Stage"
                sh 'mvn -B clean package -Dmaven.test.skip=true'
            }
        }
        stage('Image Build') {//第三步：构建镜像
            agent {
                label 'master'
            }
            steps {
                echo "3. Image Build Stage"
                sh 'docker rm -f cloud-native-project || true'
                sh 'docker rmi cloud-native-project:${BUILD_ID} || true'
                sh 'docker build -f Dockerfile --build-arg jar_name=target/cloud-native-project-0.0.1-SNAPSHOT.jar -t cloud-native-project:${BUILD_ID} . '
                //打tag是为了上传到harbor镜像仓库，可以随时使用
                sh 'docker tag cloud-native-project:${BUILD_ID} $REGISTRY/cloud-native-project:${BUILD_ID}'
            }
        }
        stage('Push') {//第四步：推送镜像
            agent {
                label 'master'
            }
            steps {
                echo "4. Push Docker Image Stage"
                sh "docker login --username=nju27 harbor.edu.cn -p nju272023"
                sh "docker push $REGISTRY/cloud-native-project:${BUILD_ID}"
            }
        }
    }
}

node('slave') {
    container('jnlp-kubectl') {
//         stage('connect'){
//             sh 'curl "http://p.nju.edu.cn/portal_io/login" --data "username=201250215&password=lyx20020305"'
//         }
        stage('Clone YAML') {//第五步：拉取代码，为了部署到k8s
            echo "5. Git clone YAML To Slave"
            git branch: 'master', url: "https://gitee.com/austynDing/cloud_-native_-project_0.git"

        }
        stage('YAML') {//第六步：替换YAML文件变量
            echo "6. Change YAML File Stage"
            sh 'sed -i "s#{VERSION}#${BUILD_ID}#g" ./jenkins/scripts/cloud-native-project.yaml'

        }
        stage('Deploy') {//第七步：部署
            echo "7. Deploy To K8s Stage"
            sh 'kubectl apply -f ./jenkins/scripts/cloud-native-project.yaml -n nju27'
            sh 'kubectl apply -f ./jenkins/scripts/cloud-native-project-serviceMonitor.yaml'
        }
//         stage('RTF Test'){//单元测试
//             echo "RTF Test Stage"
//             sh 'kubectl apply -f ./jenkins/scripts/rtf.yaml -n nju27'
//         }
    }
}