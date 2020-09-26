import tkinter as tk
from tkinter import ttk, filedialog
import tkinter.scrolledtext as tkst

from PIL import ImageTk, Image
import os
from transformation import RadonTransform, dicomFile
import numpy as np
import cv2


class CTSimulatorProgram:

    def __init__(self):
        self.window = tk.Tk()
        self.window.title("CT Simulator")
        self.window.resizable(width=False, height=False)

        self.image = Image
        self.sinogram = Image
        self.reconstruction = Image

        self.firstNameEntry = tk.Entry()
        self.middleNameEntry = tk.Entry()
        self.lastNameEntry = tk.Entry()
        self.radioVariable = tk.StringVar()
        self.birthDateEntry = tk.Entry()
        self.patientIDEntry = tk.Entry()
        self.CommentText = tk.Text()
        self.checkDateEntry = tk.Entry()
        self.CTScanFrame = ttk.LabelFrame()
        self.rmseResultLabel = tk.Entry()

        self.loadedSinogram = ttk.Label()
        self.loadedImage = ttk.Label()

        self.control = 19
        self.convolution = True
        self.gaussian = True
        self.step = 200
        self.detectors = 200
        self.width = 180

        self.radonImage = []
        self.answer = ''
        self.tmpSinograms, self.tmpReconstructions = [], []
        self.tmpRMSE = []

        self.result = Image

        self._createWidgets()

    def _createWidgets(self):
        self.window['padx'] = 5
        self.window['pady'] = 5

        # - - - - - - - - - - - - - - - - - - - - -
        # The Patient Information frame
        patientInformationFrame = ttk.LabelFrame(self.window, text=" Patient Information ", relief=tk.RIDGE)
        patientInformationFrame.grid(row=1, column=1, sticky=tk.E + tk.W + tk.N + tk.S, padx=5, pady=5)

        firstNameLabel = ttk.Label(patientInformationFrame, text="First name", width=18)
        firstNameLabel.grid(row=1, column=1, sticky=tk.W + tk.N, padx=(10, 0), pady=(10, 2))

        self.firstNameEntry = tk.Entry(patientInformationFrame, width=30)
        self.firstNameEntry.grid(row=1, column=2, columnspan=3, sticky=tk.W + tk.E, padx=(2, 10), pady=(10, 2))

        middleNameLabel = ttk.Label(patientInformationFrame, text="Middle name", width=15)
        middleNameLabel.grid(row=2, column=1, sticky=tk.W + tk.N, padx=(10, 0), pady=2)

        self.middleNameEntry = tk.Entry(patientInformationFrame, width=30)
        self.middleNameEntry.grid(row=2, column=2, columnspan=3, sticky=tk.W + tk.E, padx=(2, 10), pady=2)

        lastNameLabel = ttk.Label(patientInformationFrame, text="Last name", width=15)
        lastNameLabel.grid(row=3, column=1, sticky=tk.W + tk.N, padx=(10, 0), pady=2)

        self.lastNameEntry = tk.Entry(patientInformationFrame, width=30)
        self.lastNameEntry.grid(row=3, column=2, columnspan=3, sticky=tk.W + tk.E, padx=(2, 10), pady=2)

        sexLabel = ttk.Label(patientInformationFrame, text="Sex", width=15)
        sexLabel.grid(row=4, column=1, sticky=tk.W + tk.N, padx=(10, 0), pady=2)

        self.radioVariable = tk.StringVar()
        self.radioVariable.set("")

        maleRadioButton = tk.Radiobutton(patientInformationFrame, text="Male", variable=self.radioVariable, value="M")
        maleRadioButton.grid(row=4, column=2, sticky=tk.W, padx=2, pady=2)
        femaleRadioButton = tk.Radiobutton(patientInformationFrame, text="Female", variable=self.radioVariable,
                                           value="F")
        femaleRadioButton.grid(row=4, column=3, sticky=tk.W, padx=2, pady=2)

        birthDateLabel = ttk.Label(patientInformationFrame, text="Birth date", width=15)
        birthDateLabel.grid(row=5, column=1, sticky=tk.W + tk.E, padx=(10, 0), pady=2)

        self.birthDateEntry = tk.Entry(patientInformationFrame, width=30)
        self.birthDateEntry.grid(row=5, column=2, columnspan=3, sticky=tk.W + tk.E, padx=(2, 10), pady=2)
        self.birthDateEntry.insert(tk.END, "YYYY-DD-MM")

        patientIDLabel = ttk.Label(patientInformationFrame, text="Patient ID", width=15)
        patientIDLabel.grid(row=6, column=1, sticky=tk.W + tk.N, padx=(10, 0), pady=(2, 10))

        self.patientIDEntry = tk.Entry(patientInformationFrame, width=30)
        self.patientIDEntry.grid(row=6, column=2, columnspan=3, sticky=tk.W + tk.E, padx=(2, 10), pady=(2, 10))

        # - - - - - - - - - - - - - - - - - - - - -
        # The Health Check frame
        healthCheckFrame = ttk.LabelFrame(self.window, text=" Health Check Information ", relief=tk.RIDGE)
        healthCheckFrame.grid(row=1, column=2, sticky=tk.E + tk.W + tk.N + tk.S, padx=5, pady=5)

        checkDateLabel = ttk.Label(healthCheckFrame, text="Date of health check", width=18)
        checkDateLabel.grid(row=1, column=1, sticky=tk.W + tk.N, padx=(10, 0), pady=(10, 2))

        self.checkDateEntry = tk.Entry(healthCheckFrame, width=30, command=self.checkDateEntry.delete(0, tk.END))
        self.checkDateEntry.grid(row=1, column=2, sticky=tk.W, padx=(2, 10), pady=(10, 2))
        self.checkDateEntry.insert(0, "YYYY-DD-MM")

        CommentLabel = ttk.Label(healthCheckFrame, text="Comment", width=19)
        CommentLabel.grid(row=2, column=1, sticky=tk.W + tk.N, padx=(10, 0), pady=2)

        self.CommentText = tkst.ScrolledText(healthCheckFrame, height=7, width=28, wrap=tk.WORD)
        self.CommentText.grid(row=2, column=2, sticky=tk.W, padx=(2, 10), pady=2)
        self.CommentText.insert(tk.END, "")

        # - - - - - - - - - - - - - - - - - - - - -
        # The CT scan frame
        self.CTScanFrame = tk.LabelFrame(self.window, text=" CT Scan ", relief=tk.RIDGE)
        self.CTScanFrame.grid(row=2, column=1, columnspan=2, sticky=tk.E + tk.W + tk.N + tk.S, padx=5, pady=0)

        # Sliders - - - - - - - - - - - - - - - - -
        dataFrame = tk.Label(self.CTScanFrame, width=52, height=18)
        dataFrame.grid(row=1, column=1)

        openFrame = tk.Label(dataFrame)
        openFrame.grid(row=1, column=1, sticky=tk.W + tk.N)

        loadButton = tk.Button(openFrame, text="Open", command=self._openImage, height=2, width=6)
        loadButton.grid(row=1, rowspan=4, column=1, sticky=tk.W, padx=(30, 7), pady=(0, 0))

        convolutionCheckButton = tk.Checkbutton(openFrame, command=self._convolutionControl)
        convolutionCheckButton.grid(row=1, column=2, padx=(25, 0), pady=(0, 10))
        convolutionCheckButton.toggle()

        convolutionLabel = ttk.Label(openFrame, text="Apply convolution filter")
        convolutionLabel.grid(row=1, column=3, sticky=tk.W, pady=(0, 10))

        gaussianCheckButton = tk.Checkbutton(openFrame, command=self._gaussianControl)
        gaussianCheckButton.grid(row=2, column=2, padx=(25, 0), pady=(0, 10))
        gaussianCheckButton.toggle()

        gaussianLabel = ttk.Label(openFrame, text="Apply Gaussian filter")
        gaussianLabel.grid(row=2, column=3, sticky=tk.W, pady=(0, 10))

        progressLabel = ttk.Label(openFrame, text="Progress control:")
        progressLabel.grid(row=3, column=3, sticky=tk.W, pady=(0, 5))

        progressSlider = tk.Scale(openFrame, from_=0, to=29, orient=tk.HORIZONTAL, length=197,
                                  command=self._progressControl, showvalue=0)
        progressSlider.grid(row=4, column=3, sticky=tk.W, pady=(0, 5))
        progressSlider.set(29)

        slidersFrame = tk.Label(dataFrame)
        slidersFrame.grid(row=2, column=1, sticky=tk.N + tk.W)

        separator = ttk.Separator(slidersFrame, orient=tk.HORIZONTAL)
        separator.grid(row=2, column=1, columnspan=2, padx=10, pady=(5, 0))

        deltaAlphaLabel = ttk.Label(slidersFrame, text="Number of iterations")
        deltaAlphaLabel.grid(row=3, column=1, sticky=tk.E, padx=10, pady=2)

        deltaAlphaSlider = tk.Scale(slidersFrame, from_=90, to=720, orient=tk.HORIZONTAL, length=200,
                                    command=self._stepControl, resolution=90)
        deltaAlphaSlider.grid(row=3, column=2, sticky=tk.N, padx=2, pady=2)
        deltaAlphaSlider.set(180)

        detectorsNumberLabel = ttk.Label(slidersFrame, text="Number of detectors")
        detectorsNumberLabel.grid(row=4, column=1, sticky=tk.E, padx=10, pady=2)

        detectorsNumberSlider = tk.Scale(slidersFrame, from_=90, to=720, orient=tk.HORIZONTAL, length=200,
                                         command=self._detectorsControl, resolution=90)
        detectorsNumberSlider.grid(row=4, column=2, sticky=tk.N, padx=2, pady=2)
        detectorsNumberSlider.set(180)

        detectorsWidthLabel = ttk.Label(slidersFrame, text="Width of detectors")
        detectorsWidthLabel.grid(row=5, column=1, sticky=tk.E, padx=10, pady=(2, 0))

        detectorsWidthSlider = tk.Scale(slidersFrame, from_=45, to=270, orient=tk.HORIZONTAL, length=200,
                                        command=self._widthControl, resolution=45)
        detectorsWidthSlider.grid(row=5, column=2, sticky=tk.N, padx=2, pady=(2, 0))
        detectorsWidthSlider.set(180)

        rmseLabel = ttk.Label(slidersFrame, text="RMSE measure", foreground='grey')
        rmseLabel.grid(row=6, column=1, sticky=tk.E, padx=10, pady=(2, 10))

        self.rmseResultEntry = tk.Entry(slidersFrame, bd=0, cursor='arrow')
        self.rmseResultEntry.grid(row=6, column=2, sticky=tk.W, padx=2, pady=(2, 10))
        self.rmseResultEntry.config(state=tk.NORMAL)
        self.rmseResultEntry.insert(0, "---")
        self.rmseResultEntry.config(state=tk.DISABLED)

        v1Separator = ttk.Separator(self.CTScanFrame, orient=tk.VERTICAL)
        v1Separator.grid(row=1, column=2, pady=15, sticky=tk.N+tk.S)

        # Image - - - - - - - - - - - - - - - - - -
        self.imageFrame = tk.Label(self.CTScanFrame, width=52, height=18, text="No image to display")
        self.imageFrame.grid(row=1, column=3)

        h1Separator = ttk.Separator(self.CTScanFrame, orient=tk.HORIZONTAL)
        h1Separator.grid(row=2, column=1, columnspan=3, padx=20, sticky=tk.W+tk.E)

        # Sinogram  - - - - - - - - - - - - - - - -
        self.sinogramFrame = tk.Label(self.CTScanFrame, width=52, height=18, text="Sinogram")
        self.sinogramFrame.grid(row=3, column=1)

        v2Separator = ttk.Separator(self.CTScanFrame, orient=tk.VERTICAL)
        v2Separator.grid(row=3, column=2, pady=10, sticky=tk.N+tk.S)

        # Reconstruction  - - - - - - - - - - - - -
        self.reconstructionFrame = tk.Label(self.CTScanFrame, width=52, height=18, text="Reconstruction")
        self.reconstructionFrame.grid(row=3, column=3)

        # - - - - - - - - - - - - - - - - - - - - -
        # The Save button
        saveButton = tk.Button(self.window, text="Save", command=self._saveDICOMDocument)
        saveButton.grid(row=3, column=2, sticky=tk.E, padx=15, pady=5)

    def _openImage(self):
        openFileTypes = [('JPEG', '.jpg'), ('PNG', '.png'), ('DICOM', '.dcm')]
        self.answer = filedialog.askopenfilename(parent=self.window, initialdir=os.getcwd(),
                                                 title="Please select a file:", filetypes=openFileTypes)
        print("Opening document: " + self.answer)
        self.RMSE = []

        if self.answer[-3:] != 'dcm':
            self.image = Image.open(self.answer)
            width, height = imageResize(self.image)
            self.image = self.image.resize((width, height), Image.ANTIALIAS)
            self.image = ImageTk.PhotoImage(self.image)
            self.loadedImage = ttk.Label(self.imageFrame, image=self.image)
            self.loadedImage.grid(row=1, column=1, sticky=tk.W + tk.E + tk.N + tk.S, padx=10+(396-width)//2,
                                  pady=10+(286-height)//2)
            self._showImages()
        else:
            file = dicomFile()
            name, id, birthDate, sex, studyDate, comments, image = file.readFile(self.answer)

            self.firstNameEntry.delete(0, tk.END)
            self.middleNameEntry.delete(0, tk.END)
            self.lastNameEntry.delete(0, tk.END)
            self.birthDateEntry.delete(0, tk.END)
            self.checkDateEntry.delete(0, tk.END)
            self.CommentText.delete("1.0", tk.END)
            self.patientIDEntry.delete(0, tk.END)
            self.rmseResultEntry.delete(0, tk.END)

            name = str(name).split('^')

            birthDate = str(birthDate)[0:4] + '-' + str(birthDate)[4:6] + '-' + str(birthDate)[6:8]
            studyDate = str(studyDate)[0:4] + '-' + str(studyDate)[4:6] + '-' + str(studyDate)[6:8]

            if len(name) > 2:
                self.firstNameEntry.insert(0, name[1])
                self.middleNameEntry.insert(0, name[2])
                self.lastNameEntry.insert(0, name[0])
            else:
                self.firstNameEntry.insert(0, name[1])
                self.lastNameEntry.insert(0, name[0])

            self.birthDateEntry.insert(0, birthDate)
            self.radioVariable.set(sex)
            self.checkDateEntry.insert(0, studyDate)
            self.CommentText.insert("1.0", comments)
            self.patientIDEntry.insert(0, id)
            self.rmseResultEntry.config(state=tk.NORMAL)
            self.rmseResultEntry.delete(0, tk.END)
            self.rmseResultEntry.config(state=tk.DISABLED)

            self.loadedSinogram.grid_forget()
            self.loadedImage.grid_forget()
            self.result = image
            self.reconstruction = Image.fromarray(image)

            width, height = imageResize(self.reconstruction)
            self.reconstruction = self.reconstruction.resize((width, height), Image.ANTIALIAS)
            self.reconstruction = ImageTk.PhotoImage(self.reconstruction)
            loadedReconstruction = ttk.Label(self.reconstructionFrame, image=self.reconstruction)
            loadedReconstruction.grid(row=1, column=1, sticky=tk.W + tk.E + tk.N + tk.S, padx=10+(396-width)//2,
                                      pady=10+(286-height)//2)

    def _showImages(self):
        # Image - - - - - - - - - - - - - - - - - -
        radonTransform = RadonTransform(self.answer, self.detectors, self.width, self.step, 30)
        self.tmpSinograms, self.tmpReconstructions = radonTransform.generate(self.convolution, self.gaussian)
        self.result = self.tmpReconstructions[-1]

        for i in self.tmpReconstructions:
            self.tmpRMSE += [rmse(cv2.imread(self.answer, cv2.IMREAD_GRAYSCALE), i)]

        self.rmseResultEntry.config(state=tk.NORMAL)
        self.rmseResultEntry.delete(0, tk.END)
        self.rmseResultEntry.insert(0, str(round(self.tmpRMSE[-1], 3)))
        self.rmseResultEntry.config(state=tk.DISABLED)

        # Sinogram  - - - - - - - - - - - - - - - -
        self.sinogram = Image.fromarray(self.tmpSinograms[-1])
        width, height = imageResize(self.sinogram)
        self.sinogram = self.sinogram.resize((width, height), Image.ANTIALIAS)
        self.sinogram = ImageTk.PhotoImage(self.sinogram)
        self.loadedSinogram = ttk.Label(self.sinogramFrame, image=self.sinogram)
        self.loadedSinogram.grid(row=1, column=1, sticky=tk.W + tk.E + tk.N + tk.S, padx=10+(396-width)//2,
                                 pady=10+(286-height)//2)

        # & Reconstruction  - - - - - - - - - - - -
        self.reconstruction = Image.fromarray(self.tmpReconstructions[-1])
        width, height = imageResize(self.reconstruction)
        self.reconstruction = self.reconstruction.resize((width, height), Image.ANTIALIAS)
        self.reconstruction = ImageTk.PhotoImage(self.reconstruction)
        loadedReconstruction = ttk.Label(self.reconstructionFrame, image=self.reconstruction)
        loadedReconstruction.grid(row=1, column=1, sticky=tk.W + tk.E + tk.N + tk.S, padx=10+(396-width)//2,
                                  pady=10+(286-height)//2)

    def _showProgress(self, number):
        self.sinogram = Image.fromarray(self.tmpSinograms[number])
        width, height = imageResize(self.sinogram)
        self.sinogram = self.sinogram.resize((width, height), Image.ANTIALIAS)
        self.sinogram = ImageTk.PhotoImage(self.sinogram)
        self.loadedSinogram = ttk.Label(self.sinogramFrame, image=self.sinogram)
        self.loadedSinogram.grid(row=1, column=1, sticky=tk.W + tk.E + tk.N + tk.S, padx=10+(396-width)//2,
                                 pady=10+(286-height)//2)

        self.reconstruction = Image.fromarray(self.tmpReconstructions[number])
        width, height = imageResize(self.reconstruction)
        self.reconstruction = self.reconstruction.resize((width, height), Image.ANTIALIAS)
        self.reconstruction = ImageTk.PhotoImage(self.reconstruction)
        loadedReconstruction = ttk.Label(self.reconstructionFrame, image=self.reconstruction)
        loadedReconstruction.grid(row=1, column=1, sticky=tk.W + tk.E + tk.N + tk.S, padx=10+(396-width)//2,
                                  pady=10+(286-height)//2)

        self.rmseResultEntry.config(state=tk.NORMAL)
        self.rmseResultEntry.delete(0, tk.END)
        self.rmseResultEntry.insert(0, str(round(self.tmpRMSE[number], 3)))
        self.rmseResultEntry.config(state=tk.DISABLED)

    def _progressControl(self, control):
        self.control = int(float(control))
        if self.answer != '':
            self._showProgress(self.control)

    def _stepControl(self, step):
        self.step = int(step)
        if self.answer != '':
            self._showImages()

    def _detectorsControl(self, detectors):
        self.detectors = int(detectors)
        if self.answer != '':
            self._showImages()

    def _widthControl(self, width):
        self.width = int(width)
        if self.answer != '':
            self._showImages()

    def _convolutionControl(self):
        self.convolution = not self.convolution
        if self.answer != '':
            self._showImages()

    def _gaussianControl(self):
        self.gaussian = not self.gaussian
        if self.answer != '':
            self._showImages()

    def _saveDICOMDocument(self):
        saveFileTypes = [('DICOM', '.dcm')]
        answer = filedialog.asksaveasfilename(parent=self.window, initialdir=os.getcwd(),
                                              title="Please select a file name for saving:", filetypes=saveFileTypes)
        print("Saving document: " + answer)

        if self.middleNameEntry.get() != "":
            name = self.lastNameEntry.get() + '^' + self.firstNameEntry.get() + '^' + self.middleNameEntry.get()
        else:
            name = self.lastNameEntry.get() + '^' + self.firstNameEntry.get()
        number = self.patientIDEntry.get()
        birthDate = str(self.birthDateEntry.get())[0:4] + self.birthDateEntry.get()[5:7] + self.birthDateEntry.get()[8:]
        sex = self.radioVariable.get()
        checkDate = str(self.checkDateEntry.get())[0:4] + self.checkDateEntry.get()[5:7] + self.checkDateEntry.get()[8:]
        comment = self.CommentText.get("1.0", tk.END)

        file = dicomFile()
        file.saveFile(answer, name, number, birthDate, sex, checkDate, self.result, comment)


def imageResize(image):
    width, height = image.size
    if height / width > 286 / 396:
        w = int(width * 286 / height)
        h = 286
    else:
        w = 396
        h = int(height * 396 / width)
    return w, h


def rmse(orginalImage, reconstructedImage):
    value = np.sqrt(np.mean((orginalImage - reconstructedImage) ** 2))
    return value


def main():
    program = CTSimulatorProgram()
    program.window.mainloop()


if __name__ == "__main__":
    main()
