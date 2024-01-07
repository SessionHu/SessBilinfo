echo -e "Processing files from last build..."
rm -r build/
mkdir build/


echo -e "Building..."

lsb_release
if [ $? -eq 0 ]; then
    export cp="./lib/gson-2.10.1.jar:./lib/jansi-2.4.1.jar:./lib/jline-3.24.1.jar:./lib/jna-5.14.0.jar"
else
    export cp="./lib/gson-2.10.1.jar;./lib/jansi-2.4.1.jar;./lib/jline-3.24.1.jar;./lib/jna-5.14.0.jar"
fi

javac -encoding utf-8 \
      -Xlint:deprecation -XDignore.symbol.file -Xdiags:verbose \
      -d build/ \
      -cp $cp \
      -sourcepath src/java/tk/xhuoffice/sessbilinfo/ \
      src/java/tk/xhuoffice/sessbilinfo/net/*.java \
      src/java/tk/xhuoffice/sessbilinfo/ui/*.java \
      src/java/tk/xhuoffice/sessbilinfo/util/*.java \
      src/java/tk/xhuoffice/sessbilinfo/*.java

if [ $? -eq 0 ]; then
    echo -e "Packing..."
    cp ./NOTES.md ./README.md ./LICENSE ./RELEASE.md build/
    cd build/
    jar -cvfm 'SessBilinfo.jar' ../manifest -C ./ .
    cd ..
    echo -e "Done!"
    exit 0
else
    echo -e "Build failed!"
    exit 1
fi
