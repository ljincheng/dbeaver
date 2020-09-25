#!/bin/bash

echo "从dbeaver源码库更新最新代码"
git fetch upstream

echo "合并更新dbeaver的最新代码到本地库"
git merge upstream/devel

#### 以下是保存本地的库代码到github 的ljincheng/dbeaver库的devel分支中  ####
# git add .
# git commit -m "合并最新代码"
# git push
