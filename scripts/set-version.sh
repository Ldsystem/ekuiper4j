#!/bin/bash

# Script to set the version in all POMs

# Check if a version was provided
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <new-version>"
    echo "Example: $0 1.0.0"
    exit 1
fi

NEW_VERSION=$1
ROOT_DIR=$(dirname "$(dirname "$(readlink -f "$0")")")

# Use the 'revision' property in the root POM
sed -i '' "s/<revision>.*<\/revision>/<revision>$NEW_VERSION<\/revision>/g" "$ROOT_DIR/pom.xml"
sed -i '' "s/<version>.*-SNAPSHOT<\/version>/<version>$NEW_VERSION-SNAPSHOT<\/version>/g" "$ROOT_DIR/pom.xml"

# Clean and compile to apply the new version
echo "Updating version to $NEW_VERSION"
cd "$ROOT_DIR" && mvn clean compile

echo "Version updated to $NEW_VERSION"
echo "You can now build the project with: mvn clean install" 