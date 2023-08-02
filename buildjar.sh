echo -e "Processing files from last build..."
rm -r build/
mkdir build/ \
      build/tk/ \
      build/tk/xhuoffice/ \
      build/tk/xhuoffice/sessbilinfo/

echo -e "Building..."
javac -encoding utf-8 \
      -d build/ \
      -cp lib/gson-3.10.1.jar \
      -sourcepath src/java/tk/xhuoffice/sessbilinfo/ \
      src/java/tk/xhuoffice/sessbilinfo/Http.java \
      src/java/tk/xhuoffice/sessbilinfo/JsonLib.java \
      src/java/tk/xhuoffice/sessbilinfo/UserInfo.java \
      src/java/tk/xhuoffice/sessbilinfo/Main.java

if [ $? -eq 0 ]; then
    echo -e "Packing..."
    cp ./README.md ./LICENSE build/
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


