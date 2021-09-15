from os import listdir
from os.path import isfile, join, splitext

files = [splitext(f)[0] for f in listdir('.') if isfile(join('.', f)) and f.startswith("give_")]

with open('giveall.mcfunction', 'w+') as output:
    for file in  files:
        output.write('function mazeescape:special_items/' + file + '\n')