import sys
import os
import argparse

from PIL import Image

def resize(path, args, name):
    directory = os.listdir(path)
    for idx, item in enumerate(directory):
        try:
            img = Image.open(os.path.join(path, item))
            img = img.resize((args.width, args.height), Image.ANTIALIAS)
            img = img.convert('RGB')
            img.save(f'{args.save_folder}/{name}_{idx}.jpg', 'JPEG', quality=95)
        except:
            print(f'Image {item} failed')
        if (idx % 20 == 0):
            print(f'Resized {idx} of {len(directory)} images')
    print('Finished')


def main():
    FOLDER = sys.argv[1]
    PATH = os.getcwd()
    FOLDER_PATH = os.path.join(PATH, FOLDER)
    SAVE_FOLDER = FOLDER + '_resized'
    try:
        os.mkdir(SAVE_FOLDER)
    except:
        pass

    parser = argparse.ArgumentParser()
    parser.add_argument('-width', default=256, type=int)
    parser.add_argument('-height', default=256, type=int)
    parser.add_argument('-save_folder', default=SAVE_FOLDER, type=str)

    args, _ = parser.parse_known_args()

    resize(FOLDER_PATH, args, FOLDER)

if __name__ == '__main__':
    main()
