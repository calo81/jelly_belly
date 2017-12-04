
```
export SBT_OPTS="-XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=2G -Xmx2048M"
sbt
```


To compile react assets

From the `assets/javascripts` folder
```
browserify -t [ babelify --presets [ react ] ] 
```
