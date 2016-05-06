import React,{ requireNativeComponent, Component, PropTypes, View, Platform } from 'react-native';

if(Platform.OS === 'android') {
    var MPRadarChart = requireNativeComponent('MPRadarChart', RadarChart);
}

class RadarChart extends Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <MPRadarChart {...this.props} data={this.props.data}/>
        );
    }
}

RadarChart.propTypes = {
    ...View.propTypes,
    data: PropTypes.object,
    description: PropTypes.string,

}

export default RadarChart;