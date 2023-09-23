javac -encoding utf-8 \
      -Xlint:deprecation -XDignore.symbol.file \
      -d build/ \
      -cp lib/gson-2.10.1.jar \
      -sourcepath src/java/tk/xhuoffice/sessbilinfo/ \
      src/java/tk/xhuoffice/sessbilinfo/util/*.java \
      src/java/tk/xhuoffice/sessbilinfo/*.java

if [ $? -eq 0 ]; then
    cp ./NOTES.md ./README.md ./LICENSE ./RELEASE.md build/
    cd build/
    jar -cvfm 'SessBilinfo.jar' ../manifest -C ./ .
    exit 0
else
    echo -e "Build failed!"
    exit 1
fi