import React, { useState } from 'react';
import {
  Container,
  Typography,
  Box,
  Grid,
  Card,
  CardContent,
  TextField,
  Button,
  Avatar,
  Divider,
  Tab,
  Tabs,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Chip,
  Alert,
  IconButton,
} from '@mui/material';
import {
  Person,
  Email,
  Phone,
  LocationOn,
  ShoppingBag,
  Security,
  Edit,
  Save,
  Cancel,
} from '@mui/icons-material';
import { useAuth } from '../hooks/useAuth';
import toast from 'react-hot-toast';

function TabPanel(props) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`profile-tabpanel-${index}`}
      aria-labelledby={`profile-tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ p: { xs: 2, sm: 3 } }}>{children}</Box>}
    </div>
  );
}

const Profile = () => {
  const { user } = useAuth();
  const [tabValue, setTabValue] = useState(0);
  const [editMode, setEditMode] = useState(false);
  const [formData, setFormData] = useState({
    firstName: user?.firstName || 'John',
    lastName: user?.lastName || 'Doe',
    email: user?.email || 'john.doe@example.com',
    phone: user?.phone || '+1 (555) 123-4567',
    address: user?.address || '123 Main St, City, State 12345',
  });

  // Mock order data
  const orders = [
    {
      id: 'ORD-001',
      date: '2024-01-15',
      status: 'Delivered',
      total: 299.99,
      items: 2,
    },
    {
      id: 'ORD-002',
      date: '2024-01-10',
      status: 'Shipped',
      total: 159.99,
      items: 1,
    },
    {
      id: 'ORD-003',
      date: '2024-01-05',
      status: 'Processing',
      total: 89.99,
      items: 3,
    },
  ];

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  const handleInputChange = (field) => (event) => {
    setFormData({
      ...formData,
      [field]: event.target.value,
    });
  };

  const handleSave = () => {
    // Here you would typically make an API call to update user profile
    toast.success('Profile updated successfully!');
    setEditMode(false);
  };

  const handleCancel = () => {
    setFormData({
      firstName: user?.firstName || 'John',
      lastName: user?.lastName || 'Doe',
      email: user?.email || 'john.doe@example.com',
      phone: user?.phone || '+1 (555) 123-4567',
      address: user?.address || '123 Main St, City, State 12345',
    });
    setEditMode(false);
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'Delivered':
        return 'success';
      case 'Shipped':
        return 'info';
      case 'Processing':
        return 'warning';
      default:
        return 'default';
    }
  };

  return (
    <Container maxWidth="lg" sx={{ py: { xs: 2, sm: 4 } }}>
      <Typography 
        variant="h4" 
        component="h1" 
        gutterBottom
        sx={{ 
          fontSize: { xs: '1.75rem', sm: '2rem', md: '2.125rem' },
          mb: { xs: 2, sm: 3 },
        }}
      >
        My Profile
      </Typography>

      <Grid container spacing={{ xs: 2, md: 3 }}>
        {/* Profile Overview Card */}
        <Grid item xs={12} md={4}>
          <Card sx={{ mb: { xs: 2, md: 0 } }}>
            <CardContent sx={{ textAlign: 'center', p: { xs: 2, sm: 3 } }}>
              <Avatar
                sx={{
                  width: { xs: 80, sm: 100 },
                  height: { xs: 80, sm: 100 },
                  mx: 'auto',
                  mb: 2,
                  fontSize: { xs: '2rem', sm: '2.5rem' },
                }}
              >
                {formData.firstName.charAt(0)}{formData.lastName.charAt(0)}
              </Avatar>
              <Typography 
                variant="h6" 
                gutterBottom
                sx={{ fontSize: { xs: '1.1rem', sm: '1.25rem' } }}
              >
                {formData.firstName} {formData.lastName}
              </Typography>
              <Typography 
                color="text.secondary" 
                gutterBottom
                sx={{ fontSize: { xs: '0.8rem', sm: '0.875rem' } }}
              >
                {formData.email}
              </Typography>
              <Chip 
                label="Premium Member" 
                color="primary" 
                size="small" 
                sx={{ mt: 1 }} 
              />
            </CardContent>
          </Card>
        </Grid>

        {/* Profile Details */}
        <Grid item xs={12} md={8}>
          <Card>
            <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
              <Tabs 
                value={tabValue} 
                onChange={handleTabChange}
                variant="scrollable"
                scrollButtons="auto"
                sx={{
                  '& .MuiTab-root': {
                    fontSize: { xs: '0.8rem', sm: '0.875rem' },
                    minWidth: { xs: 'auto', sm: 160 },
                  },
                }}
              >
                <Tab label="Personal Info" />
                <Tab label="Order History" />
                <Tab label="Security" />
              </Tabs>
            </Box>

            {/* Personal Information Tab */}
            <TabPanel value={tabValue} index={0}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                <Typography 
                  variant="h6"
                  sx={{ fontSize: { xs: '1.1rem', sm: '1.25rem' } }}
                >
                  Personal Information
                </Typography>
                {!editMode ? (
                  <IconButton onClick={() => setEditMode(true)} color="primary">
                    <Edit />
                  </IconButton>
                ) : (
                  <Box>
                    <IconButton onClick={handleSave} color="primary" sx={{ mr: 1 }}>
                      <Save />
                    </IconButton>
                    <IconButton onClick={handleCancel} color="error">
                      <Cancel />
                    </IconButton>
                  </Box>
                )}
              </Box>

              <Grid container spacing={{ xs: 2, sm: 3 }}>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="First Name"
                    value={formData.firstName}
                    onChange={handleInputChange('firstName')}
                    disabled={!editMode}
                    sx={{
                      '& .MuiInputBase-input': {
                        fontSize: { xs: '0.9rem', sm: '1rem' },
                      },
                    }}
                  />
                </Grid>
                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="Last Name"
                    value={formData.lastName}
                    onChange={handleInputChange('lastName')}
                    disabled={!editMode}
                    sx={{
                      '& .MuiInputBase-input': {
                        fontSize: { xs: '0.9rem', sm: '1rem' },
                      },
                    }}
                  />
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Email"
                    value={formData.email}
                    onChange={handleInputChange('email')}
                    disabled={!editMode}
                    sx={{
                      '& .MuiInputBase-input': {
                        fontSize: { xs: '0.9rem', sm: '1rem' },
                      },
                    }}
                  />
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Phone"
                    value={formData.phone}
                    onChange={handleInputChange('phone')}
                    disabled={!editMode}
                    sx={{
                      '& .MuiInputBase-input': {
                        fontSize: { xs: '0.9rem', sm: '1rem' },
                      },
                    }}
                  />
                </Grid>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Address"
                    value={formData.address}
                    onChange={handleInputChange('address')}
                    disabled={!editMode}
                    multiline
                    rows={2}
                    sx={{
                      '& .MuiInputBase-input': {
                        fontSize: { xs: '0.9rem', sm: '1rem' },
                      },
                    }}
                  />
                </Grid>
              </Grid>
            </TabPanel>

            {/* Order History Tab */}
            <TabPanel value={tabValue} index={1}>
              <Typography 
                variant="h6" 
                gutterBottom
                sx={{ fontSize: { xs: '1.1rem', sm: '1.25rem' } }}
              >
                Order History
              </Typography>
              <List>
                {orders.map((order) => (
                  <ListItem
                    key={order.id}
                    sx={{
                      border: 1,
                      borderColor: 'divider',
                      borderRadius: 1,
                      mb: 2,
                      flexDirection: { xs: 'column', sm: 'row' },
                      alignItems: { xs: 'flex-start', sm: 'center' },
                      p: { xs: 2, sm: 2 },
                    }}
                  >
                    <ListItemIcon sx={{ minWidth: { xs: 'auto', sm: 56 }, mr: { xs: 0, sm: 2 } }}>
                      <ShoppingBag />
                    </ListItemIcon>
                    <ListItemText
                      sx={{ flex: 1 }}
                      primary={
                        <Box sx={{ 
                          display: 'flex', 
                          justifyContent: 'space-between', 
                          flexDirection: { xs: 'column', sm: 'row' },
                          gap: { xs: 1, sm: 0 },
                        }}>
                          <Typography 
                            variant="subtitle1"
                            sx={{ fontSize: { xs: '0.9rem', sm: '1rem' } }}
                          >
                            Order #{order.id}
                          </Typography>
                          <Chip
                            label={order.status}
                            color={getStatusColor(order.status)}
                            size="small"
                          />
                        </Box>
                      }
                      secondary={
                        <Box sx={{ mt: 1 }}>
                          <Typography 
                            variant="body2" 
                            color="text.secondary"
                            sx={{ fontSize: { xs: '0.8rem', sm: '0.875rem' } }}
                          >
                            Date: {order.date} • Items: {order.items} • Total: ${order.total}
                          </Typography>
                        </Box>
                      }
                    />
                  </ListItem>
                ))}
              </List>
            </TabPanel>

            {/* Security Tab */}
            <TabPanel value={tabValue} index={2}>
              <Typography 
                variant="h6" 
                gutterBottom
                sx={{ fontSize: { xs: '1.1rem', sm: '1.25rem' } }}
              >
                Security Settings
              </Typography>
              <List>
                <ListItem
                  sx={{
                    border: 1,
                    borderColor: 'divider',
                    borderRadius: 1,
                    mb: 2,
                    p: { xs: 2, sm: 2 },
                  }}
                >
                  <ListItemIcon>
                    <Security />
                  </ListItemIcon>
                  <ListItemText
                    primary={
                      <Typography sx={{ fontSize: { xs: '0.9rem', sm: '1rem' } }}>
                        Change Password
                      </Typography>
                    }
                    secondary={
                      <Typography 
                        variant="body2" 
                        color="text.secondary"
                        sx={{ fontSize: { xs: '0.8rem', sm: '0.875rem' } }}
                      >
                        Update your password to keep your account secure
                      </Typography>
                    }
                  />
                  <Button 
                    variant="outlined" 
                    size="small"
                    sx={{ fontSize: { xs: '0.8rem', sm: '0.875rem' } }}
                  >
                    Change
                  </Button>
                </ListItem>
                
                <Alert severity="info" sx={{ mt: 2 }}>
                  <Typography sx={{ fontSize: { xs: '0.8rem', sm: '0.875rem' } }}>
                    For additional security features, please contact our support team.
                  </Typography>
                </Alert>
              </List>
            </TabPanel>
          </Card>
        </Grid>
      </Grid>
    </Container>
  );
};

export default Profile;
