echo -e "Processing files from last build..."
rm -r build/
mkdir build/

echo -e "Building..."
javac -encoding utf-8 \
      -Xlint:deprecation \
      -d build/ \
      -cp lib/gson-3.10.1.jar \
      -sourcepath src/java/tk/xhuoffice/sessbilinfo/ \
      src/java/tk/xhuoffice/sessbilinfo/util/Logger.java \
      src/java/tk/xhuoffice/sessbilinfo/util/CookieFile.java \
      src/java/tk/xhuoffice/sessbilinfo/util/Http.java \
      src/java/tk/xhuoffice/sessbilinfo/util/JsonLib.java \
      src/java/tk/xhuoffice/sessbilinfo/util/OutFormat.java \
      src/java/tk/xhuoffice/sessbilinfo/util/Error.java \
      src/java/tk/xhuoffice/sessbilinfo/util/AvBv.java \
      src/java/tk/xhuoffice/sessbilinfo/Video.java \
      src/java/tk/xhuoffice/sessbilinfo/Search.java \
      src/java/tk/xhuoffice/sessbilinfo/Account.java \
      src/java/tk/xhuoffice/sessbilinfo/UserInfo.java \
      src/java/tk/xhuoffice/sessbilinfo/Main.java

if [ $? -eq 0 ]; then
    echo -e "Packing..."
    cp ./NOTES.md ./README.md ./LICENSE ./RELEASE.md build/
    unzip lib/gson-3.10.1.jar "com/*" -d build/
    cd build/
    jar -cvfm 'SessBilinfo.jar' ../manifest -C ./ .
    cd ..
    echo -e "Done!"
    exit 0
else
    echo -e "Build failed!"
    exit 1
fi

