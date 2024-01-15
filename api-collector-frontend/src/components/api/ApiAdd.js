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
import HelpIcon from '@material-ui/icons/Help';
import Tooltip from '@material-ui/core/Tooltip';
import InputLabel from '@material-ui/core/InputLabel';
import FormControl from '@material-ui/core/FormControl';
import FormHelperText from '@material-ui/core/FormHelperText';
import NativeSelect from '@material-ui/core/NativeSelect';
import { makeStyles } from '@material-ui/core/styles';
import { withStyles } from '@material-ui/core/styles';

const useStyles = makeStyles((theme) => ({
    formControl: {
        margin: theme.spacing(0),
        minWidth: 120,
    },
    selectEmpty: {
        marginTop: theme.spacing(2),
    },
}));

const ApiAddTooltip = withStyles((theme) => ({
    tooltip: {
      backgroundColor: '#f5f5f9',
      color: 'rgba(0, 0, 0, 0.87)',
      maxWidth: 260,
      fontSize: theme.typography.pxToRem(12),
      border: '2px solid #56574fc4',
    },
  }))(Tooltip);

const urlHelper = `파라미터 부분을 제외한 얖부분의 URL등록 ex) https://opendart.fss.or.kr/api/exctvSttus.json`;

function Alert(props) {
return <MuiAlert elevation={6} variant="filled" {...props} />;
}

export default function ApiAdd (props) {

    const classes = useStyles();
    const [open, enableOpen] = useState(false);
    const [apiId, setApiId] = useState();
    const [apiName, setApiName] = useState();
    const [url, setUrl] = useState();
    const [cert, setCert] = useState([{certId:''}]);
    const [certList, setCertList] = useState([]);
    const [output_format, setOutput_format] = useState();
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
        setApiId("api_".concat(moment().valueOf()))
        setApiName('')
        setUrl('')
        setCert([{certId:''}])
        setCertList([])
        setOutput_format("JSON")
        enableOpen(true)
        loadCertList()
    }
    const handleClose = () => {
        setApiId('')
        setApiName('')
        setUrl('')
        setCert([{certId:''}])
        setCertList([])
        setOutput_format('')
        enableOpen(false)
    }

    // useEffect(()=> {
    //     loadCertList()
    // }, [])

    function loadCertList() {
        axios.get(global.config.ajax.backend.common.url+'/readAllCert',{})
        .then((result) => {
            console.log(result.data)
            setCertList(result.data)
        })
        .catch(e => {
            console.error(e)
            setCertList([])
        })
    }

    function addApi(e) {
        e.preventDefault()
        const requestUrl = global.config.ajax.backend.common.url+'/createApi';
        const request = {
        'apiId': apiId,
        'apiName': apiName,
        'url': url,
        'cert': cert,
        'output_format': output_format
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
                props.loadApiList()
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
            API 추가
        </Button>
        <Dialog open={open} onClose={handleClose}>
            <DialogTitle>API 추가</DialogTitle>
            <DialogContent>
                <TextField label="apiId" type="text" name="apiId" value={apiId} disabled /><br />
                <TextField label="api명" type="text" name="apiName" value={apiName} onChange={(e)=>{setApiName(e.target.value)}} /><br />
                <TextField label="url" type="text" name="url" value={url} onChange={(e)=>{setUrl(e.target.value)}} />
                <br />
                <ApiAddTooltip title={urlHelper} arrow><HelpIcon className="helper" fontSize="small"/></ApiAddTooltip>
                <br />
                <FormControl className={classes.formControl}>
                    <InputLabel htmlFor="age-native-helper">인증키</InputLabel>
                    <NativeSelect
                    value={cert[0].certId}
                    onChange={(e)=>{
                        setCert([{certId:e.target.value}])
                    }}
                    // onClick={()=>{loadCertList()}}
                    inputProps={{
                        name: 'cert',
                        id: 'age-native-helper',
                    }}
                    >
                    <option aria-label="None" value=""></option>
                    {certList.map((oneCert)=>(
                        <option key={oneCert.certId} value={oneCert.certId}>{oneCert.certId}</option>
                    ))}           
                    </NativeSelect>
                    <FormHelperText>인증키관리에서 등록된 인증키 조회</FormHelperText>
                </FormControl> <br /><br />
                <TextField label="출력포멧" type="text" name="output_format" value={output_format} disabled /><br />
            </DialogContent>
            <DialogActions>
                <Button variant="contained" color="primary" onClick={(e)=>{addApi(e)}}>추가</Button>
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