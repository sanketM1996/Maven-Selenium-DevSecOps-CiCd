#!/bin/bash

set -e

# Update system
apt-get update -y
apt-get upgrade -y

# Install required packages
apt-get install -y \
    ca-certificates \
    curl \
    gnupg \
    lsb-release \
    wget \
    unzip

# =========================================================
# INSTALL DOCKER
# =========================================================

install -m 0755 -d /etc/apt/keyrings

curl -fsSL https://download.docker.com/linux/ubuntu/gpg \
    -o /etc/apt/keyrings/docker.asc

chmod a+r /etc/apt/keyrings/docker.asc

echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}") stable" \
  > /etc/apt/sources.list.d/docker.list

apt-get update -y

apt-get install -y \
    docker-ce \
    docker-ce-cli \
    containerd.io \
    docker-buildx-plugin \
    docker-compose-plugin

systemctl enable docker
systemctl start docker

# =========================================================
# INSTALL JAVA 21
# =========================================================

apt-get install -y openjdk-21-jdk

java -version

# =========================================================
# INSTALL JENKINS
# =========================================================

curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io-2026.key \
    -o /usr/share/keyrings/jenkins-keyring.asc

echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] \
    https://pkg.jenkins.io/debian-stable binary/ \
    > /etc/apt/sources.list.d/jenkins.list

apt-get update -y

apt-get install -y jenkins

systemctl enable jenkins
systemctl start jenkins

# =========================================================
# ALLOW JENKINS TO USE DOCKER
# =========================================================

usermod -aG docker jenkins

systemctl restart jenkins

# =========================================================
# INSTALL TRIVY
# =========================================================

wget -qO - https://aquasecurity.github.io/trivy-repo/deb/public.key \
    | gpg --dearmor \
    -o /usr/share/keyrings/trivy.gpg

echo "deb [signed-by=/usr/share/keyrings/trivy.gpg] \
    https://aquasecurity.github.io/trivy-repo/deb \
    generic main" \
    > /etc/apt/sources.list.d/trivy.list

apt-get update -y

apt-get install -y trivy

# =========================================================
# VERIFY INSTALLATIONS
# =========================================================

echo "=============================="
echo "Docker:"
docker --version

echo "=============================="
echo "Java:"
java -version

echo "=============================="
echo "Jenkins:"
systemctl status jenkins --no-pager

echo "=============================="
echo "Trivy:"
trivy --version

echo "=============================="
echo "Installation completed!"

# =========================================================
# INSTALL CHECKOV
# =========================================================

apt-get install -y python3 python3-pip python3-venv

python3 -m venv /opt/checkov

/opt/checkov/bin/pip install --upgrade pip

/opt/checkov/bin/pip install checkov

ln -sf /opt/checkov/bin/checkov /usr/local/bin/checkov

checkov --version

# =========================================================
# INSTALL GITLEAKS
# =========================================================

GITLEAKS_VERSION="8.28.0"

wget -q \
    "https://github.com/gitleaks/gitleaks/releases/download/v${GITLEAKS_VERSION}/gitleaks_${GITLEAKS_VERSION}_linux_x64.tar.gz" \
    -O /tmp/gitleaks.tar.gz

tar -xzf /tmp/gitleaks.tar.gz \
    -C /usr/local/bin gitleaks

chmod +x /usr/local/bin/gitleaks

gitleaks version