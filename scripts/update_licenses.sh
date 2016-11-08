#!/bin/sh
license_output=./app/build/reports/license/dependency-license.html
assets_folder=./app/src/main/assets/

cd ..
./gradlew downloadLicenses
# cat ./app/build/reports/license/license-dependency.xml

cp $license_output $assets_folder/license.html
