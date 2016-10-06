@echo off
color 2
echo Generating Parser with Coco...
del Parser.java
del Scanner.java
java -jar Coco.jar OESI.atg
copy Parser.java ..\src\org\xeroserver\OESI\Parser.java
copy Scanner.java ..\src\org\xeroserver\OESI\Scanner.java
pause >nul