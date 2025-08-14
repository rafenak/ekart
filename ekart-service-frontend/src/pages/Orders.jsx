import React from 'react';
import { Container, Typography, Box } from '@mui/material';

const Orders = () => {
  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        Order History
      </Typography>
      <Box sx={{ p: 4, backgroundColor: 'grey.100', borderRadius: 2 }}>
        <Typography variant="body1">
          Orders page coming soon...
        </Typography>
      </Box>
    </Container>
  );
};

export default Orders;
