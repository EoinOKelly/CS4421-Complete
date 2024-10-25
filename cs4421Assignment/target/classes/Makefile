.PHONY: libsysinfo/libsysinfo.so

MODULES=com.team1.sysInfo com.team1.cpuInfo com.team1.pciInfo com.team1.usbInfo
HEADERS=$(MODULES:=.h)

MYPROJECT=com.team1.template

all: $(HEADERS) com.team1.template.class libsysinfo/libsysinfo.so

$(MYPROJECT).class: $(MYPROJECT).java libsysinfo/libsysinfo.so
	javac $<

$(HEADERS): %.h: %.java
	javac -h . $<

libsysinfo/libsysinfo.so:
	$(MAKE) -C libsysinfo

