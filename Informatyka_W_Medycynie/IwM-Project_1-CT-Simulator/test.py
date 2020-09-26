
import cv2
import numpy as np
import math

class RadonTransform:


    def __init__(self,image,angle,detectors,detectorsWidth,iterations):
        self.inputImage=image
        self.angle = angle
        self.detectors = detectors
        self.detectorsWidth=detectorsWidth/detectors
        self.height,self.width = image.shape
        self.allLines=[]
        self.sinogram =None
        self.outputImage=None
        self.iterations=iterations
        self.sinogramImage=None
        self.outputImages=[]
        print('Rozdzielczość zdjęcia',self.width,self.height)

    def generate(self,filter):
        if(filter==True):
            self.filterGenerator(self.detectors)
        currentAngle=0
        maxwidth=int(np.sqrt((self.width)**2+(self.height)**2))/2
        pi180=np.pi/180
        self.outputArray=np.zeros((self.width,self.height))
        self.sinogram=np.zeros((self.iterations,self.detectors+1))
        m=int(self.detectors/2)
        for j in range(0,self.iterations):
            print(j)
            sinIteration=np.zeros((self.detectors+1))
            itArray=np.zeros((self.width,self.height))
            lines=[]
            for i in range(int(-m),int(m)+1):
                x0=maxwidth*np.cos((currentAngle+self.detectorsWidth*i)*pi180)
                y0=maxwidth*np.sin((currentAngle+self.detectorsWidth*i)*pi180)
                x1=maxwidth*np.cos((currentAngle+180-self.detectorsWidth*i)*pi180)
                y1=maxwidth*np.sin((currentAngle+180-self.detectorsWidth*i)*pi180)
                x0=int(x0+self.height/2)
                x1=int(x1+self.height/2)
                y0=int(y0+self.width/2)
                y1=int(y1+self.width/2)
                #generate line
                line=self.bersenhamAlgorithm(x0,y0,x1,y1)
                lines.append(line)
                sinIteration[i+m]=self.colorline(self.inputImage,line)
            if(filter==True):
               sinIteration=self.singoramFilter(sinIteration)
            for i in range(int(-m),int(m)+1):
                for point in lines[i+m]:
                    if point[0]>=0 and point[1]>=0 and point[0]<self.height and point[1] <self.width:
                        itArray[point[1]][point[0]]+=sinIteration[i+m]
            self.sinogram[j]+=sinIteration
            currentAngle+=self.angle
            self.outputArray+=itArray
        self.sinogramImage=normalize(self.sinogram)
        showImage(self.sinogramImage)
        showImage((normalize(cv2.GaussianBlur(self.outputArray,(3,3),3))))
        return self.sinogramImage


    def bersenhamAlgorithm(self,x1,y1,x2,y2):
        output=[]
        x = x1
        y = y1
        if(x1<x2):
            xi=1
            dx=x2-x1
        else:
            xi=-1
            dx=x1-x2
        if(y1<y2):
            yi=1
            dy=y2-y1
        else:
            yi=-1
            dy=y1-y2
        #print(x,y,x2,y2)
        output.append([x,y])
        if(dx>dy):
            ai=(dy-dx)*2
            bi=dy*2
            d=bi-dx
            while(x!=x2):
                if(d>=0):
                    x+=xi
                    y+=yi
                    d+=ai
                else:
                    d+=bi
                    x+=xi
                #print(x,y)
                output.append([x,y])
                
        else:
            ai=(dx-dy)*2
            bi=dx*2
            d=bi-dy
            while(y!=y2):
                if(d>=0):
                    x+=xi
                    y+=yi
                    d+=ai
                else:
                    d+=bi
                    y+=yi
                #print(x,y)
                output.append([x,y])
        return output
            
    def colorline(self,image,line):
        sum=0
        for i in line :
            if i[0]>=0 and i[1]>=0 and i[0]<self.height and i[1] <self.width:
                sum+=image[i[0]][i[1]]
        return sum
    
  
    def singoramFilter(self,iteration):
        iteration=np.convolve(iteration,self.filter,'same')
        return iteration

    def filterGenerator(self,x):
        self.filter=[]
        for i in range(-int(x/2),int((x/2))+1):
            if i%2:
                self.filter.append((-4/(np.pi**2))/(i**2))
            else:
                self.filter.append(0)
        self.filter[int(x/2)]=1
        
def normalize(array):
    image=np.transpose(array)
    mx=np.max(image)
    for y in range(len(image)):
        for x in range(len(image[0])):
            if(image[y][x]<=0):
                image[y][x]=0
            else:
                image[y][x]-=image[0][0]
                image[y][x]=image[y][x]*255/mx
    image=cv2.normalize(image, None, 0, 255, cv2.NORM_MINMAX)
    print(image)
    image=np.array(image, dtype='uint8')
    return image

def showImage(image):
    
    cv2.imshow('image',image)
    cv2.waitKey(0)
    cv2.destroyAllWindows()


    
def toSquare(image):
    x, y = image.shape[:2]
    top, bottom, left, right = 0, 0, 0, 0
    difference = math.fabs(x - y)
    if x > y:
        left = difference // 2
        right = difference - left
    elif y > x:
        top = difference // 2
        bottom = difference - top
    image = cv2.copyMakeBorder(image, top, bottom, left, right, cv2.BORDER_CONSTANT)
    return image


def main():
    image = cv2.imread('sample/Shepp_logan.jpg',cv2.IMREAD_GRAYSCALE)
    radonTransform=RadonTransform(image,180/200,200,180,200)
    radonTransform.generate(False)


if __name__=="__main__":
    main()
