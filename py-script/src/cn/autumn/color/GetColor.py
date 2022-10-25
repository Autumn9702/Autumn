from Qwindow import Ui_mainWindow
from PyQt5.QtWidgets import QApplication,QMainWindow,QPushButton
import sys
from PyQt5.QtCore import Qt
from ctypes import *  # 获取屏幕上某个坐标的颜色
import pyautogui as pag
from threading import Timer



class RepeatingTimer(Timer):
    def run(self):
        while not self.finished.is_set():
            self.function(*self.args, **self.kwargs)
            self.finished.wait(self.interval)


class Window(QMainWindow):
    def __init__(self):
        super(Window, self).__init__()
        self.ui = Ui_mainWindow()
        self.ui.setupUi(self)
        self.ui.pushButton_25.clicked.connect(self.get_color)
        self.ui.pushButton_1.clicked.connect(self.slot1)
        self.ui.pushButton_2.clicked.connect(self.slot1)
        self.ui.pushButton_3.clicked.connect(self.slot1)
        self.ui.pushButton_4.clicked.connect(self.slot1)
        self.ui.pushButton_5.clicked.connect(self.slot1)
        self.ui.pushButton_6.clicked.connect(self.slot1)
        self.ui.pushButton_7.clicked.connect(self.slot1)
        self.ui.pushButton_8.clicked.connect(self.slot1)
        self.ui.pushButton_9.clicked.connect(self.slot1)
        self.ui.pushButton_10.clicked.connect(self.slot1)
        self.ui.pushButton_11.clicked.connect(self.slot1)
        self.ui.pushButton_12.clicked.connect(self.slot1)
        self.ui.start = False
        self.ui.t = 0
        self.ui.buttonrgb = [(255,255,255)]*12
        self.ui.buttonrgb1 = [(1,1,1)]*12
        self.ui.buttonrgb1 = [('#FFFFFF')]*12
        self.ui.setcounts = 0

    def get_color(self):
        self.ui.t = RepeatingTimer(0.2,self.Get_color)
        self.ui.t.start()

    def Get_color(self):
        self.ui.start = True
        x, y = pag.position()  # 返回鼠标的坐标
        gdi32 = windll.gdi32
        user32 = windll.user32
        hdc = user32.GetDC(None)  # 获取颜色值
        pixel = gdi32.GetPixel(hdc, x, y)  # 提取RGB值
        r = pixel & 0x0000ff
        g = (pixel & 0x00ff00) >> 8
        b = pixel >> 16
        self.ui.rgb = (r, g, b)
        self.ui.rgb1 = (round(r/255,2) ,round(g/255,2),round(b/255,2))
        self.ui.rgb2 = (hex(r*16*16*16*16+g*16*16+b))
        self.ui.toolButton.setStyleSheet("background-color: rgb"+str(self.ui.rgb))

    def set_colors(self):

        exec('self.ui.pushButton_' + str(self.ui.setcounts) + '.setStyleSheet('+'\"background-color: rgb'+str(self.ui.rgb)+'\")')
        self.ui.lineEdit.setText(str(list(self.ui.rgb)))
        self.ui.lineEdit_2.setText(str(list(self.ui.rgb1)))
        self.ui.lineEdit_3.setText(('#'+str(self.ui.rgb2).strip('0x')).upper())

    def slot1(self):
        def round2(x):
            return round(x,2)
        RGB = self.sender().palette().button().color().getRgb()
        RGB1 = self.sender().palette().button().color().getRgbF()
        RGB = RGB[0:3]
        RGB1 = RGB1[0:3]
        RGB1 = tuple(map(round2,RGB1))
        self.ui.lineEdit.setText(str(list(RGB)))
        self.ui.lineEdit_2.setText(str(list(RGB1)))
        self.ui.lineEdit_3.setText(('#'+str(hex(RGB[0]*256*256+RGB[1]*256+RGB[2])).strip('0x')).upper())
        self.ui.toolButton.setStyleSheet("background-color: rgb"+str(RGB))



    def keyPressEvent(self, event):
        if self.ui.start ==False:
            return
        elif event.key() == Qt.Key_Enter-1:
            self.ui.t.cancel()
            self.ui.start = False
            self.ui.setcounts = self.ui.setcounts%12 +1
            self.set_colors()
            self.ui.buttonrgb[self.ui.setcounts%12 -1] = self.ui.rgb
            self.ui.buttonrgb1[self.ui.setcounts%12 -1] = self.ui.rgb1


if __name__ == "__main__":
    app = QApplication(sys.argv)
    Qwindow = Window()
    Qwindow.show()
    sys.exit(app.exec_())
