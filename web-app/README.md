
```
export SBT_OPTS="-XX:+CMSClassUnloadingEnabled -Xmx2048M"
sbt
```


To compile react assets

From the `assets/javascripts` folder
```
browserify -t [ babelify --presets [ react ] ]  jelly_belly/ab_tests.jsx -o'../../public/javascripts/jelly_belly.js'
```

## How to us it

Run it with ``sbt start``

Then you can create an experiment by visiting the URL

```
localhost:9000/experiments/algun_nombre/variants/a:33,b:33,c:34
```

To participate in an experiment

```
http://localhost:9000/experiments/experimento/participate/some_participant
```

Then just visiting `localhost:9000` You shuld be able to see the existing experiments.