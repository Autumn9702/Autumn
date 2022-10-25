import colorsys


def get_image_color(image) -> tuple:
    """ Get image RGB """
    mc = 0.0001
    image_color = None
    ic = image.getcolors(image.size[0] * image.size[1])
    for count, (r, g, b) in ic:
        # To hsv
        hsv = colorsys.rgb_to_hsv(r / 255.0, g / 255.0, b / 255.0)[0]
        y = min(abs(r * 2104 + g * 4130 + b * 802 + 4096 + 131072) >> 13, 235)
        y = (y - 16.0) / (235 - 16)
        # Ignore the high light
        if y > 0.9:
            continue
        s = (hsv + 0.1) * count
        if s > mc:
            mc = s
            image_color = (r, g, b)
    return image_color


def rgb_to_hex(r) -> str:
    """ RGB to hexadecimal """
    rgb = r
    hs = '#'
    for i in rgb:
        num = int(i)
        # joining together hexadecimal
        hs += str(hex(num))[-2:].replace('x', '0').upper()
    return hs


def rgb_to_hex_unsigned(r) -> int:
    hs = ''
    for i in r:
        num = int(i)
        # joining together hexadecimal
        hs += str(hex(num))[-2:].replace('x', '0').upper()

    try:
        return int(hs)
    except Exception as e:
        return 200000

