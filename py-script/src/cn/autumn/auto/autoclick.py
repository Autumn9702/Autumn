import pyautogui
import time
import pytesseract
import extract_color
from PIL import ImageGrab, Image
from tkinter import messagebox

p1Path = r'.\resource\py\autoclick\p1.jpg'
p2Path = r'.\resource\py\autoclick\p2.jpg'
execute_status = ['']


def close_task() -> None:
    execute_status[0] = 'stop'


def start_task() -> None:
    execute_status[0] = 'running'


def get_mouse_point() -> tuple:
    position = pyautogui.position()
    return position.x, position.y


def save_p_one(img_size, path):
    img1 = ImageGrab.grab(img_size)
    img1.save(path, 'JPEG')


def save_p_one_by_size(image_size):
    img1 = ImageGrab.grab(image_size)
    img1.save(p1Path, 'JPEG')


def save_p_two(img_size):
    img2 = ImageGrab.grab(img_size)
    img2.save(p2Path, 'JPEG')
    return img2


def save_to_p_one(img):
    img.save(p1Path, 'JPEG')


def get_img_rgb_num(path) -> int:
    img2 = Image.open(path)
    convert = img2.convert('RGB')
    rb = extract_color.get_image_color(convert)
    return extract_color.rgb_to_hex_unsigned(rb)


def judge_p_one_and_p_two_same(err_number, nor_or_err, err_vals, err_size):
    img2 = Image.open(p2Path)
    if nor_or_err:
        convert = img2.convert('RGB')
        rb = extract_color.get_image_color(convert)
        rg = extract_color.rgb_to_hex_unsigned(rb)
        if rg == err_number:
            reset(err_vals, err_size)
            return False
    p1 = pytesseract.image_to_string(Image.open(p1Path), lang='chi_sim')
    p2 = pytesseract.image_to_string(img2, lang='chi_sim')
    if p1 == p2:
        return True
    else:
        return False


def cycle_read():
    curr_time = int(time.time())
    while True:
        two = save_p_two()
        same = judge_p_one_and_p_two_same()
        if same:
            time.sleep(1)
            differ = int(time.time()) - curr_time
            if differ > 20:
                return
            continue
        else:
            save_to_p_one(two)
            return


def reset(err_vals, err_size):
    curr_time = int(time.time())
    for v in err_vals:
        pyautogui.click(v['x'], v['y'])
        time.sleep(1)
        i2 = save_p_two(err_size)
        same = judge_p_one_and_p_two_same(None, False, None, None)
        if int(time.time()) - curr_time > 20:
            if same:
                same = False
        if same:
            time.sleep(0.5)
            print('photo is same. sleep one second.')
            continue
        else:
            save_to_p_one(i2)
            print('No same')
            curr_time = int(time.time())


def auto_click(nor_vals, err_vals, nor_size, err_size, err_num) -> None:
    time.sleep(3)
    curr = int(time.time())
    while True:
        for v in nor_vals:
            if execute_status[0] == 'stop':
                messagebox.showinfo(title='Close', message='Task closed!')
                return
            pyautogui.click(v['x'], v['y'])
            time.sleep(1)
            i2 = save_p_two(nor_size)
            if err_vals is None or nor_size is None:
                same = judge_p_one_and_p_two_same(None, False, None, None)
            else:
                same = judge_p_one_and_p_two_same(err_num, True, err_vals, err_size)
            if int(time.time()) - curr > 20:
                if same:
                    same = False
            if same:
                time.sleep(0.5)
                print('photo is same. sleep one second.')
                continue
            else:
                save_to_p_one(i2)
                print('No same')
                curr = int(time.time())


def get_point():
    time.sleep(3)
    position = pyautogui.position()
    print('x=', position.x, ' y=', position.y)
    time.sleep(3)
    position = pyautogui.position()
    print('x=', position.x, ' y=', position.y)
    time.sleep(3)
    position = pyautogui.position()
    print('x=', position.x, ' y=', position.y)


