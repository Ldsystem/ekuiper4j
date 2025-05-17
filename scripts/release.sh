#!/bin/bash

# Script to perform a release

# Check if a version was provided
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <release-version>"
    echo "Example: $0 1.0.0"
    exit 1
fi

RELEASE_VERSION=$1
ROOT_DIR=$(dirname "$(dirname "$(readlink -f "$0")")")

# 1. Set the version number by removing -SNAPSHOT
CURRENT_VERSION=$(grep -o "<revision>[^<]*" "$ROOT_DIR/pom.xml" | cut -d ">" -f 2)
if [[ $CURRENT_VERSION != *"-SNAPSHOT"* ]]; then
    echo "Current version $CURRENT_VERSION is not a SNAPSHOT version. Cannot release."
    exit 1
fi

# Set non-snapshot version for release
RELEASE_VERSION=${CURRENT_VERSION%-SNAPSHOT}
if [ "$1" != "auto" ]; then
    RELEASE_VERSION=$1
fi

# 2. Update the version number
"$ROOT_DIR/scripts/set-version.sh" $RELEASE_VERSION

# 3. Commit the version change
git add "$ROOT_DIR/pom.xml" "$ROOT_DIR/"*"/pom.xml"
git commit -m "chore: prepare release v$RELEASE_VERSION"

# 4. Tag the release
git tag -a "v$RELEASE_VERSION" -m "Release version $RELEASE_VERSION"

# 5. Build and test
cd "$ROOT_DIR" && mvn clean install -P release

# 6. Set the next development version
NEXT_VERSION=$(echo $RELEASE_VERSION | awk -F. '{$NF = $NF + 1;} 1' | sed 's/ /./g')
"$ROOT_DIR/scripts/set-version.sh" "$NEXT_VERSION"

# 7. Commit the new development version
git add "$ROOT_DIR/pom.xml" "$ROOT_DIR/"*"/pom.xml"
git commit -m "chore: prepare for next development iteration $NEXT_VERSION"

echo "Release $RELEASE_VERSION completed successfully!"
echo "Next development version is set to $NEXT_VERSION-SNAPSHOT"
echo "Push changes with: git push && git push --tags" 