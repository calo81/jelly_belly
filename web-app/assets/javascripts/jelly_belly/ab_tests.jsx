import React from 'react';
import ReactDOM from 'react-dom';


class Echo extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            experiments: []
        };
    }

    componentDidMount() {
        // this is an "echo" websocket service
        this.connection = new WebSocket('ws://localhost:9000/sockets/experiments');

        this.connection.onopen = evt => {
            this.connection.send("subscribe")
        }
        // listen to onmessage event
        this.connection.onmessage = evt => {
            // add the new message to state
            if (evt.data != "subscribed") {
                var data = JSON.parse(evt.data)
                this.setState({
                    experiments: this.state.experiments.concat([data.experiment.experimentId])
                })
            }
        };

    }

    render() {
        alert(this.state.experiments)
        // slice(-5) gives us the five most recent messages
        return <ul>{this.state.experiments.slice(-5).map((msg, idx) => <li key={'msg-' + idx}>{msg}</li>)}</ul>;
    }
}

class NewExperimentToggler extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            shown: false
        };
        this.toggle = this.toggle.bind(this);
    }

    toggle(event) {
        var current_value = this.state.shown
        this.setState({shown: !current_value});
    }

    render() {
        return (
            <div>
                <input type="button" value="+" onClick={this.toggle}/>
                {this.state.shown ? <NewExperiment/> : null}
            </div>
        );
    }

}

class NewExperiment extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            name: ''
        };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange(event) {
        this.setState({[event.target.name]: event.target.value});
    }

    handleSubmit(event) {
        var url = "http://localhost:9000/experiments/" + this.state.name + "/variants/" + this.state.variant_1_name + ":" + this.state.variant_1_value
        fetch(url)
        event.preventDefault();
    }

    render() {
        return (
            <form onSubmit={this.handleSubmit}>
                <div>
                    Name:
                    <input type="text" name="name" value={this.state.value} onChange={this.handleChange}/>
                </div>
                <div>
                    Variants (Name and Percentage):
                    <div>
                        <input type="text" name="variant_1_name" value={this.state.value} onChange={this.handleChange}/>
                        <input type="text" name="variant_1_value" value={this.state.value}
                               onChange={this.handleChange}/>
                    </div>
                    <div>

                        <input type="text" name="variant_2_name" value={this.state.value} onChange={this.handleChange}/>
                        <input type="text" name="variant_2_value" value={this.state.value}
                               onChange={this.handleChange}/>
                    </div>
                    <div>

                        <input type="text" name="variant_3_name" value={this.state.value} onChange={this.handleChange}/>
                        <input type="text" name="variant_3_value" value={this.state.value}
                               onChange={this.handleChange}/>
                    </div>
                    <div>

                        <input type="text" name="variant_4_name" value={this.state.value} onChange={this.handleChange}/>
                        <input type="text" name="variant_4_value" value={this.state.value}
                               onChange={this.handleChange}/>
                    </div>
                    <div>

                        <input type="text" name="variant_5_name" value={this.state.value} onChange={this.handleChange}/>
                        <input type="text" name="variant_5_value" value={this.state.value}
                               onChange={this.handleChange}/>
                    </div>
                </div>
                <input type="submit" value="Submit"/>
            </form>
        );
    }
}

ReactDOM.render(
    <Echo/>,
    document.getElementById('app')
);

ReactDOM.render(
    <NewExperimentToggler/>,
    document.getElementById('newExperimentForm')
);

