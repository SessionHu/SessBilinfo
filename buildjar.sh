echo -e "Processing files from last build..."
rm -r build/
mkdir build/ \
      build/tk/ \
      build/tk/xhuoffice/ \
      build/tk/xhuoffice/sessbilinfo/

echo -e "Building..."
javac -encoding utf-8 \
      -d build/ \
      -sourcepath src/java/tk/xhuoffice/sessbilinfo/ \
      src/java/tk/xhuoffice/sessbilinfo/Http.java \
      src/java/tk/xhuoffice/sessbilinfo/Json.java \
      src/java/tk/xhuoffice/sessbilinfo/UserInfo.java \
      src/java/tk/xhuoffice/sessbilinfo/Main.java

if [ $? -eq 0 ]; then
    echo -e "Packing..."
    cp ./README.md ./LICENSE build/
    cd build/
    jar -cvfm 'SessBilinfo.jar' ../manifest -C ./ .
    cd ..
    exit 0
else
    echo "Build failed!"
    exit 1
fi


