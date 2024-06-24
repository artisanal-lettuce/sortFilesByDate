# sortFilesByDate
Java application to sort files by their creation date. 

Java application to sort files (and folders) by their creation date. Files will be copied to folders named according to mode selected (year; year and month; year, month, and day). Any files without a creation date will be copied to a 'nodate' folder. Recursive option can be given to sort any files in the subfolders, instead of sorting the folders.

### USAGE:
```java -jar sortFiles.jar -i /path/to/input/folder -o /path/to/output/folder [-y/-ym/-ymd, -r]```
<br>
To use in command line, download the .jar file in the Releases section to the right (-->)

### Options:
**mandadory**

```-i``` path to the folder with the files to be sorted

```-o``` path to the folder for the organized files

<br>

**organization modes**

```-y / --year``` organizes files by year

```-ym / --yearmonth``` organizes files by year and month (DEFAULT)

```-ymd / --yearmonthday``` organizes files by year, month and day

<br>

**optional**

```-r / --recursive``` also parses any subfolders it finds

### Images:
![image](https://github.com/artisanal-lettuce/sortFilesByDate/assets/169831610/ba361bf6-e891-4ef8-960e-677af46de35a)
