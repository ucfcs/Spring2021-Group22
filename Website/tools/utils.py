import json
import os
import csv

# https://stackoverflow.com/a/34325723
# Print iterations progress
def printProgressBar(iteration, total, prefix='', suffix='', decimals=1, length=100, fill='█', printEnd='\r'):
    '''
    Call in a loop to create terminal progress bar
    @params:
        iteration   - Required  : current iteration (Int)
        total       - Required  : total iterations (Int)
        prefix      - Optional  : prefix string (Str)
        suffix      - Optional  : suffix string (Str)
        decimals    - Optional  : positive number of decimals in percent complete (Int)
        length      - Optional  : character length of bar (Int)
        fill        - Optional  : bar fill character (Str)
        printEnd    - Optional  : end character (e.g. '\r', '\r\n') (Str)
    '''
    percent = ('{0:.' + str(decimals) + 'f}').format(100 *
                                                     (iteration / float(total)))
    filledLength = int(length * iteration // total)
    bar = fill * filledLength + '-' * (length - filledLength)
    print(f'\r{prefix} |{bar}| {percent}% {suffix}', end=printEnd)
    # Print New Line on Complete
    if iteration == total:
        print()

# write to file and increment file name if it already exists
def writeToFile(data, filename, extension='csv'):
    i = 0
    istr = ''
    while os.path.exists(f'{filename}{istr}.{extension}'):
        i += 1
        istr = str(i)

    if extension == 'json':
        with open(f'{filename}{istr}.{extension}', 'w') as outfile:
            json.dump(data, outfile)
    elif extension == 'csv':
        with open(f'{filename}{istr}.{extension}', 'w', newline="") as csv_file:
            writer = csv.writer(csv_file)
            writer.writerow(['x', 'y', 'count'])
            for tuple in data:
                writer.writerow([tuple[0][0], tuple[0][1], tuple[1]])