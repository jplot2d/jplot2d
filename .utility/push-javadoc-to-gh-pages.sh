#!/bin/bash

if [ "$TRAVIS_REPO_SLUG" == "jplot2d/jplot2d" ] && [ "$TRAVIS_JDK_VERSION" == "oraclejdk7" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "master" ]; then

  DOC_PATH=`pwd`/build/javadoc

  echo -e "Publishing javadoc from $DOC_PATH ...\n"

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/jplot2d/jplot2d gh-pages > /dev/null

  cd gh-pages
  git rm -rf ./javadoc
  mv $DOC_PATH ./javadoc
  git add -f .
  git commit -m "Lastest javadoc on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to gh-pages"
  git push -fq origin gh-pages > /dev/null

  echo -e "Published javadoc to gh-pages.\n"
  
fi
