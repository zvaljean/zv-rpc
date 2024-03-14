#!/usr/bin/env bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $SCRIPT_DIR

tag_pattern="ch-*"

# 更新最新的 tag
git fetch  --tags  --prune-tags --prune  
# 1. 找出最后的 tag
last_tag=`git ls-remote --tags nas $tag_pattern | sed '/{/d' | tail -n 1`
# 2. generator new tag
last_tag_no=`git ls-remote --tags nas $tag_pattern | sed '/{/d' | tail -n 1 | cut -d '-' -f 2-`
new_tag_no=`echo "$last_tag_no + 1 " | bc`
new_tag=`printf "ch-%d" $new_tag_no`

# 3. tag & push
if [[ -n $1 ]]; then
    git tag -a $new_tag -m "$1"
else
    git tag -a $new_tag -m "auto commmit tag"
fi

git push --tag








