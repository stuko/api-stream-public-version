import React , {useState} from 'react'
import axios from 'axios';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import TextField from '@material-ui/core/TextField';
import Button from '@material-ui/core/Button';
import Snackbar from '@material-ui/core/Snackbar';
import MuiAlert from '@material-ui/lab/Alert';
import moment from 'moment'

function Alert(props) {
return <MuiAlert elevation={6} variant="filled" {...props} />;
}

export default function CertAdd (props) {

    const [open, enableOpen] = useState(false);
    const [certId, setCertId] = useState();
    const [siteName, setSiteName] = useState('');
    const [certKey, setCertKey] = useState('');
    const [limit, setLimit] = useState('');
    const [expired, setExpired] = useState('');
    const [diogOkOpen, setDiogOkOpen] = useState(false);
    const [diogErrOpen, setDiogErrOpen] = useState(false);

    const handleOkClose = (event, reason) => {
        if (reason === 'clickaway') {
        return;
        }
        setDiogOkOpen(false);
    };
    const handleErrClose = (event, reason) => {
        if (reason === 'clickaway') {
        return;
        }
        setDiogErrOpen(false);
    };
    const handleOpen = () => {
        setCertId("cert_".concat(moment().valueOf()))
        setSiteName('')
        setCertKey('')
        setLimit('')
        setExpired('')
        enableOpen(true)
    }
    const handleClose = () => {
        setCertId('')
        setSiteName('')
        setCertKey('')
        setLimit('')
        setExpired('')
        enableOpen(false)
    }

    function addCert(e) {
        e.preventDefault()

        const requestUrl = global.config.ajax.backend.common.url+'/createCert';
        const request = {
        'certId': certId,
        'siteName': siteName,
        'certKey': certKey,
        'limit': limit,
        'expired': expired
        }
        const config = {
            headers: {
                'content-type': 'application/json'
            }
        }
        console.log(request)
        axios.post(requestUrl, request, config)
            .then((response) => {
                console.log(response.data);
                if(response.status !== 200) {
                    setDiogErrOpen(true)
                }else{
                    setDiogOkOpen(true)
                }                
                props.loadCertList()
                enableOpen(false)
            })
            .catch(error=>{
                setDiogErrOpen(true)
                console.error(error)
            })
        }
    
    return (
    <div>
        <Button variant="contained" color="primary" onClick={handleOpen}>
            인증키 추가
        </Button>
        <Dialog open={open} onClose={handleClose}>
            <DialogTitle>인증키 추가</DialogTitle>
            <DialogContent>
                <TextField label="인증키Id" type="text" name="certId" value={certId} disabled /><br />
                <TextField label="사이트명" type="text" name="siteName" value={siteName} onChange={(e)=>{setSiteName(e.target.value)}} /><br />
                <TextField label="인증키" type="text" name="certKey" value={certKey} onChange={(e)=>{setCertKey(e.target.value)}}/><br />
                <TextField label="조회제한건수(일)" type="text" name="limit" value={limit} onChange={(e)=>{setLimit(e.target.value)}} /><br />
                <TextField label="만료일(YYYYMMDD)" type="text" name="expired" value={expired} onChange={(e)=>{setExpired(e.target.value)}}/><br />
            </DialogContent>
            <DialogActions>
                <Button variant="contained" color="primary" onClick={(e)=>{addCert(e)}}>추가</Button>
                <Button variant="outlined" color="primary" onClick={handleClose}>닫기</Button>
            </DialogActions>
        </Dialog>
        <Snackbar open={diogOkOpen} autoHideDuration={6000} onClose={handleOkClose}>
            <Alert onClose={handleOkClose} severity="success">
            성공
            </Alert>
        </Snackbar>
        <Snackbar open={diogErrOpen} autoHideDuration={6000} onClose={handleErrClose}>
            <Alert onClose={handleErrClose} severity="error">
            등록 실패
            </Alert>
        </Snackbar>
    </div>
    )
}