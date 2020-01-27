setlocal
set size=%1
set filepath=%2
set path=%path%;%3

@echo %size%
@echo %filepath%

magick -size %size%  gradient: -swirl 180 %filepath%

exit 0


