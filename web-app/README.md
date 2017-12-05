
```
export SBT_OPTS="-XX:+CMSClassUnloadingEnabled -Xmx2048M"
sbt
```


To compile react assets

From the `assets/javascripts` folder
```
browserify -t [ babelify --presets [ react ] ]  jelly_belly/ab_tests.jsx -o'../../public/javascripts/jelly_belly.js'
```
