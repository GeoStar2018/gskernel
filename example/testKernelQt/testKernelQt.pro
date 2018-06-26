#-------------------------------------------------
#
# Project created by QtCreator 2017-02-20T16:46:02
#
#-------------------------------------------------

QT       += core gui

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets
unix:QMAKE_LFLAGS += -Wl,-rpath=.


CPU_TYPE=x86
unix{
    DEFINES += _HAVE_PTHREAD_
    CPU_TYPE=$$system(uname -m)
    contains(CPU_TYPE,aarch64){
        CPU_TYPE=arm64
        DEFINES+=ARM64
    }
}

CONFIG(release, debug|release){
OUT_PWD=$$PWD/../../release$$CPU_TYPE
}

CONFIG(debug, debug|release){
OUT_PWD=$$PWD/../../debug$$CPU_TYPE
}
message(output folder is $$OUT_PWD)

TARGET = testKernelQt
TEMPLATE = app


SOURCES += main.cpp\
        mytstkernel.cpp \
    showwidget.cpp \
    mapview.cpp \
    queryresult.cpp

HEADERS  += mytstkernel.h \
    showwidget.h \
    mapview.h \
    queryresult.h


CPU_TYPE=x86
unix{
    DEFINES += _HAVE_PTHREAD_
    CPU_TYPE=$$system(uname -m)
    contains(CPU_TYPE,aarch64){
        CPU_TYPE=arm64
        DEFINES+=ARM64
    }
}

win32:CONFIG(release, debug|release): LIBS += -L$$OUT_PWD  -lgsutility -lgsgeodatabase -lgsspatialreference -lgsgeometry -lgsmap -lgssymbol -lgsgeomathd -lgsqtport  -lgswin32port
else:win32:CONFIG(debug, debug|release): LIBS += -L$$OUT_PWD  -lgsutilityd -lgsgeodatabased -lgsspatialreferenced -lgsgeometryd -lgsmapd -lgssymbold -lgsgeomathdd -lgsqtport  -lgswin32portd
else:unix: LIBS += -L$$OUT_PWD -lgsutility -lgsgeodatabase -lgsspatialreference -lgsgeometry -lgsmap -lgssymbol -lgsgeomathd -lgsqtport

INCLUDEPATH += $$OUT_PWD \
                $$PWD/../../include/utility\
                $$PWD/../../include/kernel\
                $$PWD/../../include/geomathd\
                $$PWD/../../include/kernel/qt\
                $$PWD/../../include/kernel/pc

DEPENDPATH += $$OUT_PWD

RESOURCES += \
    res.qrc

FORMS += \
    queryresult.ui
