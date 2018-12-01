**IntelliJ plugin to "patch" [WEB-30653](https://youtrack.jetbrains.net/issue/WEB-30653)**

IntelliJ throws a warning when editing JavaScript modules: "Editing module's script is not supported"

As per documentation LiveEdit should at least rerun the debugger, but it does not.

This Plugin will restart the debugger whenever the above message appears.

This is not really a fix/patch, but a workaround because I cannot access/edit the sources for the Plugin.

This bug was moved to the status "Obsolete". But it is still reproducible in version:

    IntelliJ IDEA 2018.3 (Ultimate Edition)
    Build #IU-183.4284.148, built on November 21, 2018
    Licensed to XXX
    Subscription is active until XXX
    JRE: 1.8.0_152-release-1343-b15 amd64
    JVM: OpenJDK 64-Bit Server VM by JetBrains s.r.o
    Windows 10 10.0