import React from 'react';
import Avatar from '@material-ui/core/Avatar';
import Button from '@material-ui/core/Button';
import CssBaseline from '@material-ui/core/CssBaseline';
import TextField from '@material-ui/core/TextField';
import Box from '@material-ui/core/Box';
import InfoIcon from '@material-ui/icons/Info';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';
import Container from '@material-ui/core/Container';
import Copyright from '../copyright/Copyright';
import Table from '@material-ui/core/Table';
import TableBody from '@material-ui/core/TableBody';
import TableCell from '@material-ui/core/TableCell';
import TableContainer from '@material-ui/core/TableContainer';
import TableHead from '@material-ui/core/TableHead';
import TableRow from '@material-ui/core/TableRow';
import Paper from '@material-ui/core/Paper';

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

const UserInfoData = createData('k116030', '안동렬', '01011111111', '비밀번호')

function createData(id, name, phoneNumber, password) {
return { id, name, phoneNumber, password };
}

export default function UserInfo({history}) {
  const classes = useStyles();

  return (
    <Container component="main" maxWidth="xs">
      <CssBaseline />
      <div className={classes.paper}>
        <Avatar className={classes.avatar}>
          <InfoIcon/>
        </Avatar>
        <Typography component="h1" variant="h5">
          내정보
        </Typography>
        <form className={classes.form} noValidate>
        <TableContainer component={Paper}>
      <Table className={classes.table} aria-label="simple table">
        <TableHead>
          <TableRow>
            <TableCell align="center">항목</TableCell>
            <TableCell align="center">내용</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>

            <TableRow key={UserInfoData.id}>
              <TableCell component="th" scope="row" align="center">
                ID
              </TableCell>
              <TableCell align="right">
              <TextField
                variant="outlined"
                margin="normal"
                required
                fullWidth
                id="id"
                // label="Id : k사번"
                label={UserInfoData.id}
                name="id"
                autoComplete="id"
                autoFocus
            />
              </TableCell>
            </TableRow>

            <TableRow key={UserInfoData.name}>
              <TableCell component="th" scope="row" align="center">
                성명
              </TableCell>
              <TableCell align="right">
              <TextField
                variant="outlined"
                margin="normal"
                required
                fullWidth
                id="name"
                // label="Id : k사번"
                label={UserInfoData.name}
                name="name"
                autoComplete="name"
            />
              </TableCell>
            </TableRow>

            <TableRow key={UserInfoData.phoneNumber}>
              <TableCell component="th" scope="row" align="center">
                휴대폰번호
              </TableCell>
              <TableCell align="right">
              <TextField
                variant="outlined"
                margin="normal"
                required
                fullWidth
                id="phoneNumber"
                // label="Id : k사번"
                label={UserInfoData.phoneNumber}
                name="phoneNumber"
                autoComplete="phoneNumber"
            />
              </TableCell>
            </TableRow>

            <TableRow key={UserInfoData.password}>
              <TableCell component="th" scope="row" align="center">
                비밀번호
              </TableCell>
              <TableCell align="right">
              <TextField
                variant="outlined"
                margin="normal"
                required
                fullWidth
                id="password"
                label={UserInfoData.password}
                name="password"
                autoComplete="password"
            />
              </TableCell>
            </TableRow>

        </TableBody>
      </Table>
    </TableContainer>
          <Button
            type="submit"
            fullWidth
            variant="contained"
            color="primary"
            className={classes.submit}
            // onSubmit={}
          >
            내정보 변경하기
          </Button>
        </form>
      </div>
      <Box mt={8}>
        <Copyright />
      </Box>
    </Container>
  );
}