echo -e "Processing files from last build..."
rm -r build/
mkdir build/

echo -e "Building..."
javac -encoding utf-8 \
      -Xlint:deprecation -XDignore.symbol.file \
      -d build/ \
      -cp lib/gson-2.10.1.jar \
      -sourcepath src/java/tk/xhuoffice/sessbilinfo/ \
      src/java/tk/xhuoffice/sessbilinfo/net/*.java \
      src/java/tk/xhuoffice/sessbilinfo/ui/*.java \
      src/java/tk/xhuoffice/sessbilinfo/util/*.java \
      src/java/tk/xhuoffice/sessbilinfo/*.java

if [ $? -eq 0 ]; then
    echo -e "Packing..."
    cp ./NOTES.md ./README.md ./LICENSE ./RELEASE.md build/
    unzip lib/gson-2.10.1.jar -d build/
    cd build/
    rm -r META-INF
    jar -cvfm 'SessBilinfo.jar' ../manifest -C ./ .
    cd ..
    echo -e "Done!"
    exit 0
else
    echo -e "Build failed!"
    exit 1
fi

