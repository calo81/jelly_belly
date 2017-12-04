var webpack = require('webpack');
var path = require('path');
 
var BUILD_DIR = path.resolve(__dirname, '../../public/javascripts');
var APP_DIR = path.resolve(__dirname, './jelly_belly');
 
var config = {
	    entry: {
		            jelly_belly: APP_DIR + '/ab_tests.jsx'
		        },
	    output: {
		            path: BUILD_DIR,
		            filename: "[name].js"
		        },
	module : {
		        loaders : [
				            {
						                    test : /\.jsx?/,
						                    include : APP_DIR,
						                    loader : 'babel-loader'
						                }
				        ]
		    }
};
 
module.exports = config;
