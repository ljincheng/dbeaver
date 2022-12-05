#!/bin/bash

export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.0.4.1.jdk/Contents/Home

export PATH=$JAVA_HOME/bin:$PATH
java --version

mvn clean package -Dmaven.test.skip=true

echo "开始配置Mac系统JDK"
cp -r /Library/Java/JavaVirtualMachines/jdk-17.0.4.1.jdk product/community/target/products/org.jkiss.dbeaver.core.product/macosx/cocoa/x86_64/DBeaver.app/Contents/Eclipse/jre

echo "WINDOWS系统中解决源码模板中的中文乱码方法，在dbeaver.ini中添加以下配置项: -Dfile.encoding=utf-8"
## WINDOWS版本解决源码模板中的中文乱码方法，在dbeaver.ini中添加以下配置项
## -Dfile.encoding = utf-8
