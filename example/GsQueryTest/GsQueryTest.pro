#-------------------------------------------------
#
# Project created by QtCreator 2017-02-22T10:50:55
#
#-------------------------------------------------

QT       += core gui
QT += widgets
greaterThan(QT_MAJOR_VERSION, 5): QT += widgets
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


TARGET = GsQueryTest
TEMPLATE = app

SOURCES += main.cpp\
        test.cpp\
        CMap.cpp\
		queryresult.cpp

HEADERS  += test.h\
    CMap.h\
    queryresult.h


win32:CONFIG(release, debug|release): LIBS += -L$$OUT_PWD  -lgsutility -lgsgeodatabase -lgsspatialreference -lgsgeometry -lgsmap -lgssymbol -lgsqtport  -lgswin32port
else:win32:CONFIG(debug, debug|release): LIBS += -L$$OUT_PWD  -lgsutilityd -lgsgeodatabased -lgsspatialreferenced -lgsgeometryd -lgsmapd -lgssymbold -lgsqtport  -lgswin32portd
else:unix: LIBS += -L$$OUT_PWD -lgsutility -lgsgeodatabase -lgsspatialreference -lgsgeometry -lgsmap -lgssymbol -lgsqtport


INCLUDEPATH += $$OUT_PWD \
                $$PWD/../../include/utility\
                $$PWD/../../include/kernel\
                $$PWD/../../include/kernel/qt\
                $$PWD/../../include/kernel/pc


DEPENDPATH += $$OUT_PWD


FORMS    += test.ui\
			queryresult.ui
