import os
import tkinter
import pyautogui
import autoclick
import file_util
from tkinter import ttk, messagebox, DISABLED, NORMAL, END
from PIL import ImageGrab, Image, ImageTk
from time import sleep
from threading import Thread

w = 370
h = 250
sbw = 200
sbh = 120
normal = 'normal'
error = 'error'
source_folder_path = './resource/json/'
cbox_val_path = './resource/json/combobox.json'
click_val_path = './resource/json/click.json'
cbox_err_val_path = './resource/json/combobox-err.json'
click_err_val_path = './resource/json/click-err.json'
normal_var_path = './resource/json/normal.txt'
error_var_path = './resource/json/error.txt'
f1_path = r'.\resource\py\autoclick\screen.gif'
f1_err_path = r'.\resource\py\autoclick\err_judge.gif'
white_path = r'.\resource\py\autoclick\white.gif'
status = ['']
combobox_values = ['']
click_values = []
combobox_err_values = ['']
click_err_values = []

window = tkinter.Tk()
window.iconbitmap('./resource/icon/Autumn.ico')
window.title('Automatic operation')
# first is width
# Two parameter is height
screenwidth = window.winfo_screenwidth()
screenheight = window.winfo_screenheight()
window.geometry('%dx%d+%d+%d' % (w, h, (screenwidth - w) / 2, (screenheight - h) / 2))
# Allows the form size to be changed
window.resizable(False, False)


def save_file(judge):
    if judge == normal:
        file_util.json_to_file(combobox_values, cbox_val_path)
        file_util.json_to_file(click_values, click_val_path, cli_obj_json)
    else:
        file_util.json_to_file(combobox_err_values, cbox_err_val_path)
        file_util.json_to_file(click_err_values, click_err_val_path, cli_obj_json)


class ClickPosition:

    def __init__(self, name, x, y):
        self.name = name
        self.x = x
        self.y = y


def cli_obj_json(obj):
    return {
        'name': obj.name,
        'x': obj.x,
        'y': obj.y
    }


# class Step:
#     """ Add step page """
# 
#     def __init__(self, master):
#         self.master = master
#         self.point_bt = tkinter.Button(self.master, text='Get point', command=self.get_point)
#         self.step_name = tkinter.Text(self.master, width=15, height=1)
#         self.step_label = Label(self.master, text='After 5s get')
#         l_csp = csp
#         if l_csp[0] == 'click':
#             self.combobox_val = combobox_var.get()
#         else:
#             self.combobox_val = combobox_err_var.get()
#         self.point_bt_text = tkinter.StringVar()
#         self.point_bt_text.set('Get point')
#         self.init_step()
# 
#     def get_point(self) -> None:
#         """ Get point to the mouse pointer """
#         stp_name = self.step_name.get('1.0', tkinter.END)
#         if stp_name.strip() == '':
#             self.step_label['text'] = 'Name is null'
#             return
#         if stp_name in combobox_values:
#             self.step_label['text'] = 'Name existed'
#             return
#         self.step_label['text'] = 'Get success!'
#         sleep(5)
#         position = pyautogui.position()
#         cp = ClickPosition(stp_name, position.x, position.y)
#         l_csp = csp
#         if l_csp[0] == 'click':
#             if combobox_values[0].strip() == '':
#                 combobox_values.clear()
#                 combobox_values.append(stp_name)
#             click_values.append(cp)
#             save_file()
#         else:
#             if combobox_err_values[0].strip() == '':
#                 combobox_err_values.clear()
#                 combobox_err_values.append(stp_name)
#             click_err_values.append(cp)
#             save_file()
#         if self.point_bt['text'] == 'Get point':
#             self.point_bt['text'] = 'Get ok!'
#             self.point_bt['state'] = DISABLED
# 
#     def close_step(self) -> None:
#         csp.clear()
#         self.master.quit()
# 
#     def init_step(self) -> None:
#         """ Init page """
#         name_text = tkinter.Label(self.master, text='name')
#         name_text.place(x=(sbw - 40) / 2, y=2)
#         self.step_name.place(x=50, y=25)
#         self.step_label.place(x=(sbw - 70) / 2, y=50, width=80, height=20)
#         self.point_bt.place(x=(sbw - 70) / 2, y=80, width=80, height=20)


class Capture:
    """ Capture configuration """

    def __init__(self, png):
        # Get mouse x and y
        self.png_name = png
        self.sel = None
        self.X = tkinter.IntVar(value=0)
        self.Y = tkinter.IntVar(value=0)
        # Capture size
        screen_width = window.winfo_screenwidth()
        screen_height = window.winfo_screenheight()
        # Create top container
        self.top = tkinter.Toplevel(window, width=screen_width, height=screen_height)
        # To maximize and minimize buttons are not displayed
        self.top.overrideredirect(True)
        self.canvas = tkinter.Canvas(self.top, bg='white', width=screen_width, height=screen_height)
        # The full screen screenshot is displayed
        self.image = tkinter.PhotoImage(file=png)
        self.canvas.create_image(screen_width // 2, screen_height // 2, image=self.image)

        def on_left_mouse_click(event) -> None:
            """ When the mouse clicks """
            self.X.set(event.x)
            self.Y.set(event.y)
            self.sel = True

        self.canvas.bind('<Button-1>', on_left_mouse_click)

        def on_left_button_move(event) -> None:
            """ Move the left mouse button to display the selected area """
            if not self.sel:
                return
            global last_draw
            # Delete the graph you just drew
            try:
                self.canvas.delete(last_draw)
            except Exception as e:
                pass
            last_draw = self.canvas.create_rectangle(self.X.get(), self.Y.get(), event.x, event.y, outline='black')

        self.canvas.bind('<B1-Motion>', on_left_button_move)

        def on_left_button_up(event) -> None:
            """ Gets the point where the left mouse button is raised. screenshot of save area """
            self.sel = False
            try:
                self.canvas.delete(last_draw)
            except Exception as e:
                pass
            sleep(0.1)
            # Reverse is screenshot
            left, right = sorted([self.X.get(), event.x])
            top, bottom = sorted([self.Y.get(), event.y])
            pic = ImageGrab.grab((left + 1, top + 1, right, bottom))
            # Pop-up dialog box
            pic = pic.resize((150, 75))
            im = ImageTk.PhotoImage(pic)
            if self.png_name == 'temp.png':
                pic.save(f1_path, 'GIF')
                pic.save(autoclick.p1Path, 'JPEG')
                file_util.data_to_file(str((left, top, right, bottom)), normal_var_path)
                image_show.configure(image=im)
                image_show.image = im
                image_show.place(x=200, y=30)
            else:
                pic.save(f1_err_path, 'GIF')
                file_util.data_to_file(str((left, top, right, bottom)), error_var_path)
                image_show_err.configure(image=im)
                image_show_err.image = im
                image_show_err.place(x=200, y=130)
            # Close curr window
            self.top.destroy()
            autoclick.save_p_one_by_size((left, top, right, bottom))

        self.canvas.bind('<ButtonRelease-1>', on_left_button_up)
        self.canvas.pack(fill=tkinter.BOTH, expand=tkinter.YES)


def get_screen_image(path) -> Image:
    if path is not None:
        imgp = Image.open(path)
        return imgp
    imo = Image.open(f1_path)
    imo = imo.resize((150, 75))
    return imo


def read_file_to_list(cbx_v, cli_v, cbx_path, cli_path) -> tuple:
    cbx_l = file_util.read_to_list(cbx_path)
    cli_l = file_util.read_to_list(cli_path)
    if cbx_l is not None and len(cbx_l) > 0:
        cbx_v.clear()
        cli_v.clear()
        return cbx_l, cli_l
    else:
        return [''], []


def button_capture_click() -> None:
    """ Start screenshot """
    # Minimize window
    window.state('icon')
    sleep(0.2)
    filename = 'temp.png'
    im = ImageGrab.grab()
    im.save(filename)
    # Show full screen screenshot
    w = Capture(filename)
    button_capture.wait_window(w.top)
    # Screenshot end
    os.remove(filename)


def button_capture_click_err() -> None:
    """ Start screenshot """
    # Minimize window
    window.state('icon')
    sleep(0.2)
    filename = 'temp-err.png'
    im = ImageGrab.grab()
    im.save(filename)
    # Show full screen screenshot
    w_err = Capture(filename)
    button_capture.wait_window(w_err.top)
    # Screenshot end
    os.remove(filename)


# def create_step_page() -> None:
#     step = Tk()
#     step.title('Add')
#     step.geometry('%dx%d+%d+%d' % (sbw, sbh, (screenwidth - sbw) / 2, (screenheight - sbh) / 2))
#     step.resizable(False, False)
#     Step(step)
#     step.mainloop()


def add_cbx() -> None:
    judge_input_status(True)
    if n_i_lab['text'] != 'A name:':
        n_i_lab['text'] = 'A name:'
        i_i_lab['text'] = 'A index:'
    name_input.delete('0.0', END)
    index_input.delete('0.0', END)


def add_err_cbx() -> None:
    judge_input_status(True)
    if n_i_lab['text'] != 'EA name:':
        n_i_lab['text'] = 'EA name:'
        i_i_lab['text'] = 'EA index:'
    name_input.delete('0.0', END)
    index_input.delete('0.0', END)


def edit_cbx() -> None:
    get_v = combobox_var.get()
    if get_v == '':
        messagebox.showwarning(title='Err', message='Please add')
        return
    judge_input_status(True)
    if n_i_lab['text'] != 'E name:':
        n_i_lab['text'] = 'E name:'
        i_i_lab['text'] = 'E index:'
    index_input.delete('0.0', END)
    index_input.insert('0.0', str(combobox_values.index(get_v)))
    index_input.configure(state=DISABLED)
    name_input.insert('0.0', get_v)


def edit_err_cbx() -> None:
    get_v = combobox_err_var.get()
    if get_v == '':
        messagebox.showwarning(title='Err', message='Please add')
        return
    judge_input_status(True)
    if n_i_lab['text'] != 'EE name:':
        n_i_lab['text'] = 'EE name:'
        i_i_lab['text'] = 'EE index:'
    index_input.delete('0.0', END)
    index_input.insert('0.0', str(combobox_err_values.index(get_v)))
    index_input.configure(state=DISABLED)
    name_input.insert('0.0', get_v)


def get_point() -> None:
    if n_i_lab['text'] == 'A name:' or n_i_lab['text'] == 'EA name:':
        if n_i_lab['text'] == 'A name:':
            cbx_v = combobox_values
            cli_v = click_values
            cbx_var = combobox_var
        else:
            cbx_v = combobox_err_values
            cli_v = click_err_values
            cbx_var = combobox_err_var
        ipt_get = index_input.get('1.0', END)
        if ipt_get == '\n':
            index = None
        else:
            try:
                index = int(ipt_get)
            except ValueError as ve:
                messagebox.showwarning(title='Err', message='Index too number')
                return
        if index is not None and index != '':
            if index > len(cbx_v):
                messagebox.showwarning(title='Err', message='Index too much')
                return
        sleep(5)
        position = pyautogui.position()
        stp_name = name_input.get('1.0', END)
        cp = cli_obj_json(ClickPosition(stp_name, position.x, position.y))
        if cbx_v[0] == '':
            cbx_v.clear()

        if index is None:
            cbx_v.append(stp_name)
            cli_v.append(cp)
        else:
            cbx_v.insert(index, stp_name)
            cli_v.insert(index, cp)
        cbx_var.set(stp_name)
        name_input.delete('0.0', END)
        index_input.delete('0.0', END)
    else:
        if n_i_lab['text'] == 'E name:':
            cbx_v = combobox_values
            cli_v = click_values
            cbx_var = combobox_var
        else:
            cbx_v = combobox_err_values
            cli_v = click_err_values
            cbx_var = combobox_err_var
        sleep(5)
        position = pyautogui.position()
        stp_name = name_input.get('1.0', END)
        cp = cli_obj_json(ClickPosition(stp_name, position.x, position.y))
        ipt_get = index_input.get('1.0', END)
        if ipt_get == '\n':
            index = None
        else:
            index = int(ipt_get)
        cbx_v[index] = stp_name
        cli_v[index] = cp
        cbx_var.set(stp_name)
        name_input.delete('0.0', END)
        index_input.delete('0.0', END)
    if n_i_lab['text'] == 'A name:' or n_i_lab['text'] == 'E name:':
        save_file(normal)
    else:
        save_file(error)


def judge_input_status(s) -> None:
    if s:
        name_input.configure(state=NORMAL)
        index_input.configure(state=NORMAL)
        gp_bt.configure(state=NORMAL)
    else:
        name_input.configure(state=DISABLED)
        index_input.configure(state=DISABLED)
        gp_bt.configure(state=DISABLED)
        if n_i_lab['text'] != 'Name:':
            n_i_lab['text'] = 'Name:'
            i_i_lab['text'] = 'Index:'


def del_cbx() -> None:
    judge_input_status(False)
    val = combobox_var.get()
    cbx_v = combobox_values
    if val == '':
        return
    if val in cbx_v:
        cbx_v.remove(val)
        for v in click_values:
            if v['name'] == val:
                click_values.remove(v)
                break
    save_file(normal)
    if len(cbx_v) == 0:
        cbx_v.append('')
    combobox_var.set(cbx_v[0])


def del_err_cbx() -> None:
    judge_input_status(False)
    val = combobox_err_var.get()
    if val == '':
        return
    if val in combobox_err_values:
        combobox_err_values.remove(val)
        for v in click_err_values:
            if v['name'] == val:
                click_err_values.remove(v)
                save_file(error)
                break
    if len(combobox_err_values) == 0:
        combobox_err_values.append('')
        combobox_err_var.set('')
    else:
        combobox_err_var.set(combobox_err_values[0])


def start_task() -> None:
    cbx_v = combobox_values
    if len(cbx_v) == 0 or os.path.exists(normal_var_path) is False:
        messagebox.showwarning(title='Err', message='Need screenshot and step')
        return
    err_exist = os.path.exists(error_var_path)
    nor_size = file_util.file_to_tuple(normal_var_path)
    status[0] = 'running'
    autoclick.start_task()
    if err_exist:
        err_size = file_util.file_to_tuple(error_var_path)
        err_num = autoclick.get_img_rgb_num(f1_err_path)
        autoclick.auto_click(click_values, click_err_values, nor_size, err_size, err_num)
    else:
        autoclick.auto_click(click_values, None, nor_size, None, None)


def start() -> None:
    if status[0] == 'running':
        messagebox.showwarning(title='Err', message='An execution task exists. Procedure')
        return
    st = Thread(target=start_task)
    st.start()


def stop() -> None:
    if status[0] != 'running':
        messagebox.showwarning(title='Err', message='The task is not executed. Procedure')
        return
    autoclick.close_task()
    status[0] = 'stop'


""" Button """
# Normal
button_capture = tkinter.Button(window, text='Screenshot ↓', command=button_capture_click)
button_capture.place(x=235, y=3, width=80, height=20)
add_bt = tkinter.Button(window, text='Add', command=add_cbx)
add_bt.place(x=15, y=40, height=20)
edit_bt = tkinter.Button(window, text='Edit', command=edit_cbx)
edit_bt.place(x=80, y=40, height=20)
del_bt = tkinter.Button(window, text='Delete', command=del_cbx)
del_bt.place(x=138, y=40, height=20)
# Error
button_err_capture = tkinter.Button(window, text='Screenshot ↓', command=button_capture_click_err)
button_err_capture.place(x=235, y=105, width=80, height=20)
add_err_step = tkinter.Button(window, text='Add', command=add_err_cbx)
add_err_step.place(x=15, y=100, height=20)
add_err_step = tkinter.Button(window, text='Edit', command=edit_err_cbx)
add_err_step.place(x=80, y=100, height=20)
del_err_step = tkinter.Button(window, text='Delete', command=del_err_cbx)
del_err_step.place(x=138, y=100, height=20)

""" Image """
# show screenshot
if os.path.exists(f1_path):
    image_open = get_screen_image(None)
else:
    image_open = get_screen_image(white_path)
img = ImageTk.PhotoImage(image_open)
image_show = tkinter.Label(window, image=img, background='lightblue')
image_show.place(x=200, y=25)

# Err step show screenshot
if os.path.exists(f1_err_path):
    image_err_open = get_screen_image(f1_err_path)
else:
    image_err_open = get_screen_image(white_path)
img_err = ImageTk.PhotoImage(image_err_open)
image_show_err = tkinter.Label(window, image=img_err, background='red')
image_show_err.place(x=200, y=130)

""" Combobox """
# Read combobox file
cbx_name = tkinter.Label(window, text='Normal:', bg='lightblue')
cbx_name.place(x=15, y=10)
# Determine whether the folder exists
file_util.folder_exist_create(source_folder_path)
combobox_values, click_values = read_file_to_list(combobox_values, click_values, cbox_val_path, click_val_path)
combobox_var = tkinter.StringVar()
combobox = ttk.Combobox(window, textvariable=combobox_var, values=combobox_values,
                        postcommand=lambda: combobox.configure(values=combobox_values))
combobox.configure(state='readonly')
combobox.current(0)
combobox.place(x=70, y=10, width=115)

""" Err combobox """
# Combobox name
cbx_name = tkinter.Label(window, text='Error:', bg='red')
cbx_name.place(x=15, y=70)
# Read file
combobox_err_values, click_err_values = read_file_to_list(combobox_err_values, click_err_values, cbox_err_val_path, click_err_val_path)
combobox_err_var = tkinter.StringVar()
combobox_err = ttk.Combobox(window, textvariable=combobox_err_var, values=combobox_err_values,
                            postcommand=lambda: combobox_err.configure(values=combobox_err_values))
combobox_err.configure(state='readonly')
combobox_err.current(0)
combobox_err.place(x=70, y=70, width=115)

""" Information input """
# name
n_i_lab = tkinter.Label(window, text='Name:')
n_i_lab.place(x=13, y=130)
name_input = tkinter.Text(window, width=15, height=1)
name_input.configure(state=DISABLED)
name_input.place(x=75, y=135)
# index
i_i_lab = tkinter.Label(window, text='Index:')
i_i_lab.place(x=13, y=160)
index_input = tkinter.Text(window, width=15, height=1)
index_input.configure(state=DISABLED)
index_input.place(x=75, y=165)

# Add information and get point
gp_bt = tkinter.Button(window, text='Get point', command=get_point)
gp_bt.configure(state=DISABLED)
gp_bt.place(x=55, y=190, width=80, height=20)

# Start button and stop button
start = tkinter.Button(window, text='Start', command=start)
start.place(x=55, y=220, width=80, height=20)
stop = tkinter.Button(window, text='Stop', command=stop)
stop.place(x=240, y=220, width=80, height=20)
# The main loop
window.mainloop()
