echo -e "Building..."

lsb_release
if [ $? -eq 0 ]; then
    export cp="./lib/gson-2.10.1.jar:./lib/jansi-2.4.1.jar"
else
    export cp="./lib/gson-2.10.1.jar;./lib/jansi-2.4.1.jar"
fi

javac -encoding utf-8 \
      -Xlint:deprecation -XDignore.symbol.file \
      -d build/ \
      -cp $cp \
      -sourcepath src/java/tk/xhuoffice/sessbilinfo/ \
      src/java/tk/xhuoffice/sessbilinfo/net/*.java \
      src/java/tk/xhuoffice/sessbilinfo/ui/*.java \
      src/java/tk/xhuoffice/sessbilinfo/util/*.java \
      src/java/tk/xhuoffice/sessbilinfo/*.java

if [ $? -eq 0 ]; then
    cd build/
    jar -cvfm 'SessBilinfo.jar' ../manifest -C ./ .
    exit 0
else
    echo -e "Build failed!"
    exit 1
fi