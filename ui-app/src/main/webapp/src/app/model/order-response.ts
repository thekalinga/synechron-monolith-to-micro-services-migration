import {OrderStatus} from './order-status';

export interface OrderResponse {
    id: number;
    productName: string;
    quantity: number;
    status: OrderStatus;
}
