import React ,{ useState } from 'react';
import Avatar from '@material-ui/core/Avatar';
import Button from '@material-ui/core/Button';
import CssBaseline from '@material-ui/core/CssBaseline';
import TextField from '@material-ui/core/TextField';
import Box from '@material-ui/core/Box';
import LockOutlinedIcon from '@material-ui/icons/LockOutlined';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';
import Container from '@material-ui/core/Container';
import Copyright from '../copyright/Copyright';
import axios from 'axios';
import Snackbar from '@material-ui/core/Snackbar';
import Alert from 'react-bootstrap/Alert'

const typoBold = makeStyles({
  bold: {
    fontWeight: 600
  }
});

const useStyles = makeStyles((theme) => ({
  paper: {
    marginTop: theme.spacing(8),
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
  },
  avatar: {
    margin: theme.spacing(1),
    backgroundColor: theme.palette.secondary.main,
  },
  form: {
    width: '100%', // Fix IE 11 issue.
    marginTop: theme.spacing(1),
  },
  submit: {
    margin: theme.spacing(3, 0, 2),
  },
}));

export default function SignIn({history}) {
  const classes = useStyles();
  const classes2 = typoBold();

  const [id, setId] = useState('');
  const [pw, setPw] = useState('');
  const [loginOpen, setLoginOpen] = useState(false);
  const [errorOpen, setErrorOpen] = useState(false);

  const onKeyPress = (e) =>{
    if(e.key == 'Enter'){
      clickLogin()
    }
  }

  function clickLogin(){
    axios.post(global.config.ajax.backend.common.url+'/login',{"id":id, "pw":pw})
    .then((result) => {
      console.log("####### " + global.config.login);
      if(result.data.result === 'true') {
        setLoginOpen(true);
        global.config.login = true;
        document.location.href='/';
      }
      else {
        console.log("######## not login");
        setErrorOpen(true);
      }
    })
    .catch(e => {
      console.error(e);
      setErrorOpen(true);
    })

  }

  function loginClose(){
    setLoginOpen(false);
  }
  function errorClose(){
    setErrorOpen(false);
  }


  return (
    <Container component="main" maxWidth="xs">
      <CssBaseline />
      <div className={classes.paper}>
        <Avatar className={classes.avatar}>
          <LockOutlinedIcon />
        </Avatar>
        <Typography component="h3" variant="h4" color="primary" className={classes2.bold}>
          로그인
        </Typography>
        <form className={classes.form} validate="true">
          <TextField
            variant="outlined"
            margin="normal"
            required
            fullWidth
            id="id"
            label="Id"
            name="id"
            autoComplete="id"
            onKeyPress={onKeyPress}
            onChange={val => setId(val.target.value)}
            autoFocus
          />
          <TextField
            variant="outlined"
            margin="normal"
            required
            fullWidth
            name="password"
            label="Password"
            type="password"
            id="password"
            onChange={val => setPw(val.target.value)}
            onKeyPress={onKeyPress}
            autoComplete="current-password"
          />
          <Button
            type="button"
            fullWidth
            variant="contained"
            color="primary"
            className={classes.submit}
            onClick={clickLogin}
          >
            로그인
          </Button>
        </form>
      </div>
      <Snackbar open={loginOpen} autoHideDuration={6000} onClose={loginClose}>
        <Alert onClose={loginClose} severity="success">
          로그인하셨습니다.
        </Alert>
      </Snackbar>
      <Snackbar open={errorOpen} autoHideDuration={6000} onClose={errorClose}>
        <Alert onClose={errorClose} severity="error">
          죄송합니다. 로그인 아이디와 패스워드를 확인해 주세요.
        </Alert>
      </Snackbar>

      <Box mt={8}>
        <Copyright />
      </Box>
    </Container>
  );
}