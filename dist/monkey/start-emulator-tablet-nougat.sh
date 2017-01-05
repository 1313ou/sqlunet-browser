#!/bin/bash

pushd /opt/androidstudio/android-studio/bin
/opt/androidsdk/tools/qemu/linux-x86_64/qemu-system-x86_64 -netdelay none -netspeed full -avd avd_emu_tablet7_nougat
popd

