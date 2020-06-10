var path = require('path');

module.exports = {
    entry: {
//		app: './src/main/js/app.js'
		index: './src/main/js/pages/index.jsx',
		Components: './src/main/js/components/Components.jsx'
	},
	
    devtool: 'sourcemaps',
    cache: true,
    mode: 'development',

    output: {
        path: __dirname + "/src/main/resources/static/built",
        filename: '[name].js'
    },
    
    resolve: {
    	extensions: [".jsx", ".json", ".js"]
    }, 

    module: {
        rules: [
            {
                test: path.join(__dirname, '.'),
                exclude: /(node_modules)/,
                use: [{
                    loader: 'babel-loader',
                    options: {
                        presets: ["@babel/preset-env", "@babel/preset-react"]
                    }
                }]
            }
        ]
    }
};