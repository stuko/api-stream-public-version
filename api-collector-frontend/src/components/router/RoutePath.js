import React,{Component} from 'react';
import {Route , Switch} from 'react-router-dom';
import Home from "../home/Home";
import UserSignUp from "../user/UserSignUp";
import Login from "../user/Login";
import Forgot from "../user/Forgot";
import UserInfo from "../user/UserInfo";
import Schedule from "../schedule/Schedule";
import Batch from "../batch/Batch";
import Api from "../api/API";
import Cert from "../cert/Cert";
import Process from "../process/Process";
import Monitor from "../monitor/Monitor";
import Alarm from "../alarm/Alarm";
import ApiDrawer from '../menu/ApiDrawer';
import GoJs from '../gojs/Diagram';
import PageTest from '../test/PageTest';
import { withStyles } from '@material-ui/core/styles';

const styles = theme => ({
    hidden: {
        display: 'none'
    },
    api_body: {
      color: "black"
    },
  });

class RoutePath extends Component {
    render(){
        const { classes } = this.props
        return(
            <div>
            <ApiDrawer/>
            <Switch>
                <Route exact path="/" component={Home} />
                <Route exact path="/signup" component={UserSignUp} />
                <Route exact path="/login" component={Login} />
                <Route exact path="/forgot" component={Forgot} />
                <Route exact path="/userInfo" component={UserInfo} />
                <Route exact path="/schedule" component={Schedule} />
                <Route exact path="/batch" component={Batch} />
                <Route exact path="/api" component={Api} />
                <Route exact path="/cert" component={Cert} />
                <Route exact path="/process" component={Process} />
                <Route exact path="/monitor" component={Monitor} />
                <Route exact path="/alarm" component={Alarm} />
                <Route exact path="/gojs" component={GoJs} />   
                <Route exact path="/test" component={PageTest} />            
            </Switch>
            </div>
        );
    }
    

}  

export default withStyles(styles)(RoutePath)

