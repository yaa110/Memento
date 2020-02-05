#! /usr/bin/python3

import json
import base64
import os
import argparse
import io
try:
    from PIL import Image, ImageDraw, ImageFont
except ImportError:
    # PIL is not required to just extract images
    pass


parser = argparse.ArgumentParser(description='Extract Memento backups')
parser.add_argument('archive', metavar='archive', type=str,
                    help='Memento archive to extract.')
parser.add_argument('--pdf', nargs='?', metavar='SHAPE',
                    help='Group images into pdf documents: e.g., --pdf=3x4')
args = parser.parse_args()

def parse_backup(path):
    with open(path) as fin:
        cont = json.load(fin)

    sections = {c['_id'] : c for c in cont if c['_parent'] == -1}
    pages = {c['_id'] : c for c in cont if c['_parent'] != -1}

    # Classify pages in sections
    for s_id in sections:
        sections[s_id]['pages'] = []

    for p_id in sorted(pages.keys()):
        parent = sections[pages[p_id]['_parent']]
        parent['pages'].append(p_id)
        # Decode body:
        pages[p_id]['_body'] = base64.decodebytes(bytes(pages[p_id]['_body'], encoding='ASCII'))

    return sections, pages

def produce_images(sections, pages):
    for s_id in sorted(sections.keys()):
        s_name = sections[s_id]['_title']
        if not os.path.exists(s_name):
            os.mkdir(s_name)
        for p_id in sections[s_id]['pages']:
            p_name = pages[p_id]['_title']
            path = os.path.join(s_name, f"{p_name}.png")
            with open(path, 'wb') as fout:
                fout.write(pages[p_id]['_body'])

def prepare_sheet(width, height, cols, rows):
    sheet = Image.new('RGB', (cols*width, rows*height), (255, 255, 255))
    img_draw = ImageDraw.Draw(sheet)
    return sheet, img_draw

def finalize_sheet(img_draw, width, height, cols, rows):
    for i in range(1, cols):
        img_draw.line([((i)*width, 0),
                       ((i)*width, rows*height)],
              width=3, fill=(0,0,255))
    for j in range(1, rows):
        img_draw.line([(0, j*height),
                       (cols*width, j*height)],
              width=3, fill=(0,0,255))

def produce_pdf(sections, pages, shape):

    cols, rows = shape
    per_page = cols*rows

    def create_image(p_id):
        sio = io.BytesIO()
        sio.write(pages[p_id]['_body'])
        sio.seek(0)
        return Image.open(sio)

    for s_id in sorted(sections.keys()):
        s_name = sections[s_id]['_title']
        # Convert images
        for p_id in sections[s_id]['pages']:
            pages[p_id]['img'] = create_image(p_id)
        # Assume all images have the same size
        width, height = pages[p_id]['img'].size

        sheets = []

        i = -1
        j = -1

        for idx, p_id in enumerate(sections[s_id]['pages']):

            i = (i + 1) % cols
            if i == 0:
                j = (j + 1) % rows
                if j == 0:
                    sheet, img_draw = prepare_sheet(width, height, cols, rows)
                    sheets.append(sheet)

            sheet.paste(pages[p_id]['img'], (width*i, height*j))
            fnt = ImageFont.truetype('Pillow/Tests/fonts/FreeMono.ttf', 80)
            img_draw.text((i*width,(j+1)*height-80),
                          pages[p_id]['_title'], font=fnt, fill=(0,0,255,128))

            if ((idx % per_page == per_page-1) or
                (idx == len(sections[s_id]['pages']) - 1)):
                finalize_sheet(img_draw, width, height, cols, rows)

        for idx, p in enumerate(sheets):
            p.save(f"{s_name}_{idx}.pdf")

if __name__ == '__main__':
    sections, pages = parse_backup(args.archive)
    if args.pdf is not None:
        produce_pdf(sections, pages,
                    (int(i) for i in args.pdf.split('x')))
    else:
        produce_images(sections, pages)
