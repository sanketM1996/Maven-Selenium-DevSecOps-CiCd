#!/bin/bash

set -e

echo "======================================"
echo " Jenkins Installation Script"
echo "======================================"

# Check root privileges
if [ "$EUID" -ne 0 ]; then
    echo "Please run this script with sudo."
    exit 1
fi

echo "[1/6] Updating package index..."
apt update

echo "[2/6] Installing required packages..."
apt install -y fontconfig openjdk-21-jre wget

echo "[3/6] Checking Java version..."
java -version

echo "[4/6] Adding Jenkins repository key..."

mkdir -p /etc/apt/keyrings

wget -O /etc/apt/keyrings/jenkins-keyring.asc \
    https://pkg.jenkins.io/debian-stable/jenkins.io-2026.key

echo "[5/6] Adding Jenkins repository..."

echo "deb [signed-by=/etc/apt/keyrings/jenkins-keyring.asc] https://pkg.jenkins.io/debian-stable binary/" \
    > /etc/apt/sources.list.d/jenkins.list

echo "[6/6] Installing Jenkins..."

apt update
apt install -y jenkins

