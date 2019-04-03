@ECHO OFF

@REM # 从package.json恢复npm repo
@REM #-------------------------------------------------------------------------------------


IF NOT EXIST package.json (
    ECHO 找不到package.json文件，请重试
    pause
)

IF EXIST node_modules (

    ECHO 删除此前存在的node_modules中...
    ECHO ----------------------------------------------------------------------
    RD /S /Q node_modules
)

IF EXIST package-lock.json (

    ECHO 删除package-lock.json中...
    ECHO ----------------------------------------------------------------------
    DEL package-lock.json
)

IF EXIST dependencies.txt (

    ECHO 清空根目录依赖库文件中...
    ECHO ----------------------------------------------------------------------
    for /F "tokens=*" %%L in (dependencies.txt) DO (
        IF EXIST "%%L" (
            RD /S /Q "%%L"
        )
    )


    ECHO 清空dependencies.txt中...
    ECHO ----------------------------------------------------------------------
    DEL dependencies.txt
)

ECHO 开始初始化npm repository...
ECHO ----------------------------------------------------------------------
CALL npm install

ECHO 创建dependencies.txt记录拷贝的依赖包
ECHO ----------------------------------------------------------------------
break > dependencies.txt

IF EXIST node_modules (

    for /D %%F in (node_modules/*) DO (
         @ECHO %%F>> dependencies.txt
    )

    ECHO 拷贝依赖包至项目根目录中...
    ECHO ----------------------------------------------------------------------
    xcopy node_modules\* .\* /I /E /y /q

    RD /S /Q node_modules
)

ECHO ----------------------------------------------------------------------
@if "%1" == "1" PAUSE
