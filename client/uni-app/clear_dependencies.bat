@ECHO OFF

@REM # 清空项目根目录的外部依赖包目录
@REM #-------------------------------------------------------------------------------------


IF EXIST package-lock.json (

    ECHO 删除package-lock.json中...
    ECHO ----------------------------------------------------------------------
    DEL package-lock.json
)

IF NOT EXIST dependencies.txt (

    ECHO 找不到dependencies.txt文件，请重试
    PAUSE

) ELSE (

    ECHO 删除根目录的依赖包目录中...
    ECHO ----------------------------------------------------------------------

    FOR /F "tokens=*" %%L in (dependencies.txt) DO (
        IF EXIST "%%L" (
            RD /S /Q "%%L"
        )
    )

    DEL dependencies.txt
)

ECHO ----------------------------------------------------------------------
@if "%1" == "1" PAUSE