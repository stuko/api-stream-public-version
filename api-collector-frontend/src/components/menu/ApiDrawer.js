import React from 'react';
import { NavLink , Router as router , useHistory} from 'react-router-dom'
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import IconButton from '@material-ui/core/IconButton';
import MenuIcon from '@material-ui/icons/Menu';

import clsx from 'clsx';
import { makeStyles } from '@material-ui/core/styles';
import Drawer from '@material-ui/core/Drawer';
import Button from '@material-ui/core/Button';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import ScheduleIcon from '@material-ui/icons/Schedule';
import HomeIcon from '@material-ui/icons/Home';
import ViewModuleIcon from '@material-ui/icons/ViewModule';
import SettingsInputComponentIcon from '@material-ui/icons/SettingsInputComponent';
import VpnKeyIcon from '@material-ui/icons/VpnKey';

import axios from 'axios';

import './ApiDrawer.css';

  const useBold = makeStyles({
    bold:{
      fontWeight : 400,
      fontSize: '1.1em'
    }
  });
  const useStyles = makeStyles((theme) => ({
    root: {
      flexGrow: 1,
    },
    menuButton: {
      marginRight: theme.spacing(2),
    },
    title: {
      flexGrow: 1,
      fontSize: '1.8em',
      fontWeight: 500
    },
    list: {
      width: 250,
    },
    fullList: {
      width: 'auto',
    },
  }));  
  
  export default function ApiDrawer({history}) {

    const navi = useHistory();
    const boldClass = useBold();
    const classes = useStyles();
    const [state, setState] = React.useState({
      top: false,
      left: false,
      bottom: false,
      right: false,
    });

    React.useEffect(()=>{
      axios.post(global.config.ajax.backend.common.url+'/checkLogin',{})
      .then((result) => {
        console.log("before login status : {} " , global.config.login);
        if(result.data.result === 'true') {
          global.config.login = true;
          setState({ ...state});
        }else{
          global.config.login = false;
          if(window.location.href.indexOf('/login') < 0){
            document.location.href='/login';
          }
        }
        console.log("after login status : {} " , global.config.login);
      })
      .catch(e => {
        console.error(e);
        global.config.login = false;
      })
    }, []);

    const logIn = () => {
      navi.push('/login');
    }
    const logOut = () => {
      console.log(global.config.ajax.backend.common.url+'/logOut');
      axios.post(global.config.ajax.backend.common.url+'/logOut',{})
      .then((result) => {
        document.location.href='/';
      })
      .catch(e => {
        console.error(e);
        global.config.login = false;
      })
    }


    const toggleDrawer = (anchor, open) => (event) => {
      if (event.type === 'keydown' && (event.key === 'Tab' || event.key === 'Shift')) {
        return;
      }
  
      setState({ ...state, [anchor]: open });
    };
  
    const list = (anchor) => (
      <div
        className={clsx(classes.list, {
          [classes.fullList]: anchor === 'top' || anchor === 'bottom',
        })}
        role="presentation"
        onClick={toggleDrawer(anchor, false)}
        onKeyDown={toggleDrawer(anchor, false)}
      >
        <List>
          <ListItem button key='홈'>
            <ListItemIcon><HomeIcon></HomeIcon></ListItemIcon>
            <ListItemText primary={<NavLink exact to="/" className={boldClass.bold}>Home</NavLink>}/>
          </ListItem>
          <ListItem button key='스케줄관리'>
            <ListItemIcon><ScheduleIcon></ScheduleIcon></ListItemIcon>
            <ListItemText primary={<NavLink exact to="/schedule" className={boldClass.bold}>Scheduler</NavLink>}/>
          </ListItem>
          <ListItem button key='인증키관리'>
            <ListItemIcon><VpnKeyIcon></VpnKeyIcon></ListItemIcon>
            <ListItemText primary={<NavLink exact to="/cert" className={boldClass.bold}>API Cert Key</NavLink>}/>
          </ListItem>
          <ListItem button key='API관리'>
            <ListItemIcon><SettingsInputComponentIcon></SettingsInputComponentIcon></ListItemIcon>
            <ListItemText primary={<NavLink exact to="/api" className={boldClass.bold}>API Management</NavLink>}/>
          </ListItem>
          <ListItem button key='토폴로지'>
            <ListItemIcon><ViewModuleIcon></ViewModuleIcon></ListItemIcon>
            <ListItemText><a href="/index-jquery.html" className={boldClass.bold}>Topology</a></ListItemText>
          </ListItem>
        </List>
      </div>
    );
  
    return (
      <div>
        {['left'].map((anchor) => (
          <React.Fragment key={anchor}>
            <AppBar position="static">
              <Toolbar>
                <IconButton edge="start" className={classes.menuButton}
                color="inherit" aria-label="menu"
                onClick={toggleDrawer(anchor, true)}
                >
                  <MenuIcon />
                </IconButton>
                <Typography variant="h7" className={classes.title} align="center">
                DUNA SIMULATION
                </Typography>
                {!global.config.login && <Button color="inherit" onClick={logIn} >Login</Button>}
                {global.config.login && <Button color="inherit" onClick={logOut} >Logout</Button>}
              </Toolbar>
            </AppBar>
            <Drawer 
            anchor={anchor} open={state[anchor]} onClose={toggleDrawer(anchor, false)}>
              {list(anchor)}
            </Drawer>
          </React.Fragment>
        ))}
        
      </div>
    );
  }